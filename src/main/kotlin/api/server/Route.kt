package main.kotlin.api.server

import kotlin.reflect.KFunction

/**
 * Represents a routable HTTP endpoint derived from a controller method.
 *
 * Each [Route] holds metadata required to match a request URI against a
 * declared path template (e.g., `/user/{id}`), as well as to extract path
 * variables and query parameters to be passed to the controller method.
 *
 * @property pathTemplate The URI pattern (e.g., "/product/{id}").
 * @property function The Kotlin reflection reference to the controller method.
 * @property instance The controller instance that owns the method.
 * @property pathVariables Names of dynamic segments in the path (e.g., ["id"]).
 * @property queryParams Names of query parameters expected in the method.
 */
data class Route(
    val pathTemplate: String,
    val function: KFunction<*>,
    val instance: Any,
    val pathVariables: List<String>,
    val queryParams: List<String>
) {

    /**
     * Checks whether a given request path matches this route's path template.
     *
     * A match occurs if:
     * - Both path and template have the same number of segments
     * - Static segments match exactly
     * - Dynamic segments are allowed using `{name}` notation
     *
     * @param requestPath The incoming request URI path (e.g., "/product/123").
     * @return `true` if the path matches the template, `false` otherwise.
     */
    fun match(requestPath: String): Boolean {
        val tplParts = pathTemplate.split('/')
        val reqParts = requestPath.split('/')
        if (tplParts.size != reqParts.size) return false

        return tplParts.zip(reqParts).all { (tpl, actual) ->
            tpl == actual || tpl.matches("\\{\\w+\\}".toRegex())
        }
    }

    /**
     * Extracts the values of dynamic path variables from a request URI.
     *
     * For example, for a template `/user/{id}` and request `/user/42`,
     * this returns `mapOf("id" to "42")`.
     *
     * @param requestPath The incoming request URI path.
     * @return A map from variable name to its value in the request path.
     */
    fun extractPathVariables(requestPath: String): Map<String, String> {
        val tplParts = pathTemplate.split('/')
        val reqParts = requestPath.split('/')
        return tplParts.mapIndexedNotNull { idx, tpl ->
            "\\{(\\w+)\\}".toRegex().matchEntire(tpl)
                ?.groupValues?.get(1)
                ?.let { name -> name to reqParts.getOrElse(idx) { "" } }
        }.toMap()
    }
}
