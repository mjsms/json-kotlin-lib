package `Version (Gabriel)`

// Base sealed class for all JSON value types
sealed class JsonValue {
    // Each subclass must implement how it converts itself to a JSON string
    abstract fun toJson(): String
}

// Represents a JSON string: e.g., "hello"
data class JsonString(val value: String) : JsonValue() {
    override fun toJson() = "\"${value}\""
}

// Represents a JSON number: e.g., 42 or 3.14
data class JsonNumber(val value: Number) : JsonValue() {
    override fun toJson() = value.toString()
}

// Represents a JSON boolean: true or false
data class JsonBoolean(val value: Boolean) : JsonValue() {
    override fun toJson() = value.toString()
}

// Represents a JSON null
object JsonNull : JsonValue() {
    override fun toJson() = "null"
}

// Represents a JSON array: e.g., [1, "two", true]
data class JsonArray(val items: List<JsonValue>) : JsonValue() {

    // Converts each element to a JSON string and joins with commas inside brackets
    override fun toJson(): String =
        items.joinToString(prefix = "[", postfix = "]") { it.toJson() }
}

// Represents a JSON object: e.g., {"name": "Alice", "age": 30}
data class JsonObject(val fields: Map<String, JsonValue>) : JsonValue() {

    // Converts each key-value pair to a JSON string inside curly braces
    override fun toJson(): String =
        fields.entries.joinToString(prefix = "{", postfix = "}") { (key, value) ->
            "\"$key\": ${value.toJson()}"
        }
}
