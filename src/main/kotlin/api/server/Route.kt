package main.kotlin.api.server

import kotlin.reflect.KFunction

data class Route(
    val pathTemplate: String,
    val function: KFunction<*>,
    val instance: Any,
    val pathVariables: List<String>,  // nomes das variáveis {x}
    val queryParams:   List<String>   // nomes dos parâmetros ?a=...&b=...
) {
    fun match(requestPath: String): Boolean {
        val tplParts = pathTemplate.split('/')
        val reqParts = requestPath.split('/')
        if (tplParts.size != reqParts.size) return false

        return tplParts.zip(reqParts).all { (tpl, actual) ->
            tpl == actual || tpl.matches("\\{\\w+\\}".toRegex())
        }
    }

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
