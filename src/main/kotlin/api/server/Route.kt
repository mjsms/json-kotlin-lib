package main.kotlin.api.server

import java.lang.reflect.Method
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

data class Route(
    val path: String,
    val method: Method,
    val instance: Any,
    val pathVariables: List<Int> = emptyList(),
    val queryParams: List<String> = emptyList()
) {
    fun match(requestPath: String): Boolean {
        val pathParts = path.split('/')
        val requestParts = requestPath.split('/')

        if (pathParts.size != requestParts.size) return false

        return pathParts.zip(requestParts).all { (routePart, requestPart) ->
            routePart == requestPart || routePart.startsWith('(') && routePart.endsWith(')')
        }
    }

    fun extractPathVariables(requestPath: String): Map<String, String> {
        val pathParts = path.split('/')
        val requestParts = requestPath.split('/')
        val variables = mutableMapOf<String, String>()

        pathParts.forEachIndexed { index, part ->
            if (part.startsWith('(') && part.endsWith(')')) {
                val varName = part.removeSurrounding("(", ")")
                variables[varName] = requestParts[index]
            }
        }

        return variables
    }
}