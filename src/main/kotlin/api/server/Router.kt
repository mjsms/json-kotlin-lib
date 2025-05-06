package main.kotlin.api.server

import main.kotlin.api.annotations.Mapping
import main.kotlin.api.annotations.Param
import main.kotlin.api.annotations.Path
import java.lang.reflect.Method
import java.net.URI
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod

class Router(controllers: List<Any>) {
    private val routes = mutableListOf<Route>()

    init {
        controllers.forEach { registerController(it) }
    }

    private fun registerController(controller: Any) {
        val controllerClass = controller::class
        val controllerPath = controllerClass.findAnnotation<Mapping>()?.path ?: ""

        controllerClass.memberFunctions.forEach { function ->
            function.findAnnotation<Mapping>()?.let { mapping ->
                val method = function.javaMethod ?: return@let
                val fullPath = listOf(controllerPath, mapping.path)
                    .filter { it.isNotEmpty() }
                    .joinToString("/")
                    .removePrefix("/")
                    .removeSuffix("/")

                val pathVariables = method.parameters
                    .mapIndexedNotNull { index, param ->
                        param.getAnnotation(Path::class.java)?.let { index }
                    }

                val queryParams = method.parameters
                    .mapIndexedNotNull { index, param ->
                        param.getAnnotation(Param::class.java)?.let {
                            param.name to index
                        }
                    }

                routes.add(
                    Route(
                        path = fullPath,
                        method = method,
                        instance = controller,
                        pathVariables = pathVariables,
                        queryParams = queryParams.map { it.first }
                    )
                )
            }
        }
    }

    fun handleRequest(uri: URI, httpMethod: String): String {
        val path = uri.path.removePrefix("/").removeSuffix("/")
        val query = uri.query

        val route = routes.find { it.match(path) }
            ?: throw NotFoundException("Route not found")

        if (httpMethod != "GET") {
            throw NotFoundException("Method not allowed")
        }

        val pathVariables = route.extractPathVariables(path)
        val queryParams = parseQueryParams(query)

        val args = prepareArguments(route, pathVariables, queryParams)

        val result = route.method.invoke(route.instance, *args)
        return toJsonString(result)
    }

    private fun parseQueryParams(query: String?): Map<String, String> {
        if (query.isNullOrEmpty()) return emptyMap()

        return query.split('&').associate {
            val parts = it.split('=')
            parts[0] to parts.getOrElse(1) { "" }
        }
    }

    private fun prepareArguments(
        route: Route,
        pathVariables: Map<String, String>,
        queryParams: Map<String, String>
    ): Array<Any?> {
        return route.method.parameters.mapIndexed { index, param ->
            when {
                param.getAnnotation(Path::class.java) != null -> {
                    val varName = route.path.split('/')
                        .find { it.startsWith('(') && it.endsWith(')') }
                        ?.removeSurrounding("(", ")")
                    pathVariables[varName]
                }
                param.getAnnotation(Param::class.java) != null -> {
                    queryParams[param.name]
                }
                else -> null
            }?.let { convertType(it, param.type) }
        }.toTypedArray()
    }

    private fun convertType(value: String, type: Class<*>): Any {
        return when (type) {
            String::class.java -> value
            Int::class.java -> value.toInt()
            Double::class.java -> value.toDouble()
            Boolean::class.java -> value.toBoolean()
            else -> value
        }
    }

    private fun toJsonString(obj: Any?): String {
        // Usa sua biblioteca JSON existente para serialização
        // Se você já tem uma função de serialização, use-a aqui
        // Exemplo simplificado:
        return when (obj) {
            null -> "null"
            is String -> "\"$obj\""
            is Number, is Boolean -> obj.toString()
            is Iterable<*> -> obj.joinToString(",", "[", "]") { toJsonString(it) }
            is Map<*, *> -> obj.entries.joinToString(",", "{", "}") {
                "\"${it.key}\":${toJsonString(it.value)}"
            }
            else -> "\"$obj\""
        }
    }
}