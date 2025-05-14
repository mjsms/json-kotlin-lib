package json.fn

import json.model.elements.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility

/**
 * Converts a Kotlin object into its corresponding [JElement] representation
 * in the JSON model. This function performs recursive inference using reflection.
 *
 * ## Supported types:
 * - `null` → [JNull]
 * - `String` → [JString]
 * - `Int`, `Double` → [JNumber]
 * - `Boolean` → [JBoolean]
 * - `Enum` → [JString] (enum name as string)
 * - `List<*>` → [JArray] (elements converted recursively)
 * - `Map<String, *>` → [JObject] (keys must be strings; values converted recursively)
 * - Data classes → [JObject] (public properties converted recursively)
 *
 * @param value The Kotlin object to convert to the JSON model.
 * @return The root [JElement] representing the given object.
 * @throws IllegalArgumentException if a Map has a non-string key.
 */
fun toJson(value: Any?): JElement = when (value) {
    null -> JNull
    is String -> JString(value)
    is Int, is Double -> JNumber(value as Number)
    is Boolean -> JBoolean(value)
    is Enum<*> -> JString(value.name)
    is List<*> -> JArray(value.map { toJson(it) })
    is Map<*, *> -> JObject(value.entries.map {
        val key = it.key as? String ?: error("Map keys must be strings")
        JProperty(key, toJson(it.value))
    }.toMutableList())
    else -> {
        val props = value::class.members
            .filterIsInstance<KProperty1<Any, *>>()
            .filter { it.visibility == KVisibility.PUBLIC }
            .map { JProperty(it.name, toJson(it.get(value))) }
        JObject(props.toMutableList())
    }
}
