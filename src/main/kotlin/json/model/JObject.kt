package  json.model.elements

/**
 * In-memory representation of a JSON object.
 *
 * - **Immutable**: [fields] is a read-only `Map`, so the instance cannot be
 *   mutated after creation.
 * - Provides convenience operators (`get`, `plus`) to derive new objects.
 */
data class JObject(val fields: Map<String, JValue>) : JValue(), Iterable<Map.Entry<String, JValue>> {

    /* ─────────────────────────── Serialization ─────────────────────────── */

    /** Serialises this object to valid compact JSON. */
    override fun toJson(): String =
        fields.entries
            .joinToString(prefix = "{", postfix = "}") { (key, value) ->
                "\"${escape(key)}\": ${value.toJson()}"
            }

    /** Minimal escaping for JSON object keys (quotes & backslash). */
    private fun escape(raw: String): String =
        buildString {
            raw.forEach { ch ->
                when (ch) {
                    '\\' -> append("\\\\")
                    '"'  -> append("\\\"")
                    else -> append(ch)
                }
            }
        }

    /* ─────────────────────────── Convenience API ───────────────────────── */

    /** Number of key–value pairs. */
    val size: Int get() = fields.size

    /** Access value by key via `obj["name"]` */
    operator fun get(key: String): JValue? = fields[key]

    /**
     * Functional add / overwrite:
     * returns a **new** `JObject` with [key] → [value] added
     * (or replaced if [key] already existed).
     */
    operator fun plus(pair: Pair<String, JValue>): JObject =
        JObject(fields + pair)

    /**
     * Merge two objects.  
     * Keys in [other] override keys in this object when they clash.
     */
    operator fun plus(other: JObject): JObject =
        JObject(fields + other.fields)

    /** Allow `for ((k,v) in obj)` iteration. */
    override fun iterator(): Iterator<Map.Entry<String, JValue>> = fields.entries.iterator()
}
