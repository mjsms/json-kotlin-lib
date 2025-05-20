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

/**
 * Core HTTP router that maps annotated controller methods to URL paths.
 *
 * The [Router] inspects controller classes using Kotlin reflection,
 * collects all `@Mapping`-annotated methods, and builds a list of [Route]s.
 * It handles path matching, parameter extraction, type conversion, and method invocation.
 *
 * Example:
 * ```
 * @Mapping("/hello")
 * class HelloController {
 *     @Mapping("/greet")
 *     fun greet(@Param name: String) = jsonObject { "msg" to "Hello $name" }
 * }
 * ```
 *
 * @param controllers A list of controller instances to inspect and register.
 */
class Router(controllers: List<Any>) {
    private val routes = mutableListOf<Route>()

    init {
        controllers.forEach { registerController(it) }
    }

    /**
     * Registers a single controller by inspecting all of its functions for `@Mapping`.
     *
     * For each mapped method, builds a [Route] with extracted path and query variable names.
     *
     * @param controller The controller instance to inspect.
     */
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

    /**
     * Handles an incoming HTTP request by resolving the matching route,
     * extracting parameters, and invoking the target function.
     *
     * Converts the result to JSON using [toJson].
     *
     * @param uri The request URI (including path and query).
     * @param httpMethod The HTTP method (only GET is allowed).
     * @return A JSON string result from invoking the controller method.
     * @throws NotFoundException if no route matches the request.
     */
    fun handleRequest(uri: URI, httpMethod: String): String {
        if (httpMethod != "GET") throw NotFoundException("Method not allowed")

        val reqPath   = uri.path.trim('/')
        val route     = routes.find { it.match(reqPath) }
            ?: throw NotFoundException("Route not found")

        val pathVals  = route.extractPathVariables(reqPath)
        val queryVals = parseQueryParams(uri.query)

        val args = buildPositionalArgs(route, pathVals, queryVals)
        val result = route.function.call(*args.toTypedArray())

        return toJson(result).toString()
    }

    /**
     * Parses the query string into a map of parameter name → value.
     *
     * @param query The raw query string from the URI.
     * @return A map of query parameters.
     */
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

    /**
     * Builds the positional argument list for calling a controller method.
     *
     * It includes:
     * - the controller instance as the first argument (receiver)
     * - extracted and type-converted path and query parameters
     *
     * @param route The matched route to prepare arguments for.
     * @param pathVars Extracted path variable values.
     * @param queryVars Extracted query parameter values.
     * @return A list of arguments in order, to be passed to KFunction.call().
     */
    private fun buildPositionalArgs(
        route: Route,
        pathVars: Map<String, String>,
        queryVars: Map<String, String>
    ): List<Any?> {
        val fn = route.function
        val posArgs = mutableListOf<Any?>()

        // First: the receiver object
        posArgs += route.instance

        // Then: all value parameters in declared order
        fn.parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .forEach { param ->
                val raw = when {
                    param.findAnnotation<Path>()  != null -> pathVars[param.name]
                    param.findAnnotation<Param>() != null -> queryVars[param.name]
                    else -> null
                }
                val converted = raw?.let { convertType(it, param.type) }
                posArgs += converted
            }

        return posArgs
    }

    /**
     * Converts a raw string value to the expected parameter type.
     *
     * Supports basic types: String, Int, Double, Boolean.
     *
     * @param value The raw string from the URL or query.
     * @param type The expected Kotlin type.
     * @return The converted value as the correct type.
     * @throws IllegalArgumentException if the type is unsupported or conversion fails.
     */
    private fun convertType(value: String, type: kotlin.reflect.KType): Any {
        return when (type.jvmErasure) {
            String::class  -> value
            Int::class     -> value.toInt()
            Double::class  -> value.toDouble()
            Boolean::class -> value.toBoolean()
            else           -> throw IllegalArgumentException("Unsupported type: ${type.jvmErasure}")
        }
    }
}
