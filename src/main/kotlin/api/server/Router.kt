package main.kotlin.api.server

import json.fn.toJson
import main.kotlin.api.annotations.Mapping
import main.kotlin.api.annotations.Param
import main.kotlin.api.annotations.Path
import java.net.URI
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.jvmErasure

class Router(controllers: List<Any>) {
    private val routes = mutableListOf<Route>()

    init {
        controllers.forEach { registerController(it) }
    }

    private fun registerController(controller: Any) {
        val basePath = controller::class.findAnnotation<Mapping>()?.path?.trim('/') ?: ""
        controller::class.memberFunctions.forEach { fn ->
            fn.findAnnotation<Mapping>()?.let { mapping ->
                val template = listOf(basePath, mapping.path.trim('/'))
                    .filter { it.isNotEmpty() }
                    .joinToString("/")

                val pathVars = "\\{(\\w+)\\}".toRegex()
                    .findAll(template)
                    .map { it.groupValues[1] }
                    .toList()

                val queryVars = fn.parameters
                    .filter { it.findAnnotation<Param>() != null }
                    .mapNotNull { it.name }
                    .toList()

                routes += Route(
                    pathTemplate  = template,
                    function      = fn,
                    instance      = controller,
                    pathVariables = pathVars,
                    queryParams   = queryVars
                )
            }
        }
    }

    fun handleRequest(uri: URI, httpMethod: String): String {
        if (httpMethod != "GET") throw NotFoundException("Method not allowed")

        val reqPath   = uri.path.trim('/')
        val route     = routes.find { it.match(reqPath) }
            ?: throw NotFoundException("Route not found")

        val pathVals  = route.extractPathVariables(reqPath)
        val queryVals = parseQueryParams(uri.query)

        // 1) Gero lista de args posicionais
        val args = buildPositionalArgs(route, pathVals, queryVals)

        // 2) Invoco com call
        val result = route.function.call(*args.toTypedArray())

        return toJson(result).toString()
    }

    private fun parseQueryParams(query: String?): Map<String, String> {
        if (query.isNullOrEmpty()) return emptyMap()
        return query.split('&').mapNotNull {
            it.split('=', limit = 2).let { parts ->
                parts.getOrNull(0)?.takeIf { it.isNotEmpty() }?.let { name ->
                    name to parts.getOrElse(1) { "" }
                }
            }
        }.toMap()
    }

    private fun buildPositionalArgs(
        route: Route,
        pathVars: Map<String, String>,
        queryVars: Map<String, String>
    ): List<Any?> {
        val fn = route.function
        val posArgs = mutableListOf<Any?>()

        // primeiro o receiver / this
        posArgs += route.instance

        // depois cada parâmetro VALUE, na ordem declarada em `fn.parameters`
        fn.parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .forEach { param ->
                val raw = when {
                    param.findAnnotation<Path>()  != null ->
                        pathVars[param.name]

                    param.findAnnotation<Param>() != null ->
                        queryVars[param.name]

                    else -> null
                }
                val converted = raw?.let { convertType(it, param.type) }
                posArgs += converted
            }

        return posArgs
    }

    private fun convertType(value: String, type: kotlin.reflect.KType): Any {
        return when (type.jvmErasure) {
            String::class  -> value
            Int::class     -> value.toInt()
            Double::class  -> value.toDouble()
            Boolean::class -> value.toBoolean()
            else           -> throw IllegalArgumentException("Tipo não suportado: ${type.jvmErasure}")
        }
    }
}
