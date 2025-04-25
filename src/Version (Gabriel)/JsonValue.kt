// Classe selada base para todos os tipos de valores JSON
sealed class JsonValue {
    // Cada subclasse deve implementar como se converte para uma string JSON
    abstract fun toJson(): String
}

// Representa uma string JSON: ex. "olá"
data class JsonString(val value: String) : JsonValue() {
    override fun toJson() = "\"${value}\""
}

// Representa um número JSON: ex. 42 ou 3.14
data class JsonNumber(val value: Number) : JsonValue() {
    override fun toJson() = value.toString()
}

// Representa um valor booleano JSON: true ou false
data class JsonBoolean(val value: Boolean) : JsonValue() {
    override fun toJson() = value.toString()
}

// Representa um valor nulo JSON
object JsonNull : JsonValue() {
    override fun toJson() = "null"
}

// Representa um array JSON: ex. [1, "dois", true]
data class JsonArray(val items: List<JsonValue>) : JsonValue() {

    // Converte cada elemento para JSON e junta tudo com vírgulas entre parênteses retos
    override fun toJson(): String =
        items.joinToString(prefix = "[", postfix = "]") { it.toJson() }
}

// Representa um objeto JSON: ex. {"nome": "João", "idade": 25}
data class JsonObject(val fields: Map<String, JsonValue>) : JsonValue() {

    // Converte cada par chave-valor para JSON, separado por vírgulas entre chavetas
    override fun toJson(): String =
        fields.entries.joinToString(prefix = "{", postfix = "}") { (key, value) ->
            "\"$key\": ${value.toJson()}"
        }
}
