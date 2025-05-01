package model.elements

/**
 * Root of the in-memory JSON hierarchy.
 *
 * Every JSON value (object, array, string, number, boolean or null) is a
 * subclass of [JValue].  The composite types (`JObject`, `JArray`) will be
 * declared in separate files in the same package, but they also inherit from
 * this sealed class.
 *
 * @see JString
 * @see JNumber
 * @see JBoolean
 * @see JNull
 */
sealed class JValue {

    /**
     * Serialises this value to a valid JSON string.
     * Subclasses provide the concrete implementation.
     */
    abstract fun toJson(): String
}

/* ─────────────────────────── Primitive leaf types ────────────────────────── */

/** JSON string value – e.g. `"hello"` */
data class JString(val value: String) : JValue() {
    override fun toJson(): String =
        buildString {
            append('"')
            value.forEach { ch ->
                // Minimal escaping: quotes, backslash and control chars
                when (ch) {
                    '\\' -> append("\\\\")
                    '"'  -> append("\\\"")
                    '\b' -> append("\\b")
                    '\u000C' -> append("\\f")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(ch)
                }
            }
            append('"')
        }
}

/** JSON numeric value – e.g. `42` or `3.14` */
data class JNumber(val value: Number) : JValue() {
    override fun toJson(): String = value.toString()
}

/** JSON boolean – `true` or `false` */
data class JBoolean(val value: Boolean) : JValue() {
    override fun toJson(): String = value.toString()
}

/** The singleton JSON literal **null** */
object JNull : JValue() {
    override fun toJson(): String = "null"
}
