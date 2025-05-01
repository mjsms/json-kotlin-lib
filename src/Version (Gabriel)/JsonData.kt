// Classe selada base para todos os tipos de valores JSON
sealed class JValue {
    // Cada subclasse deve implementar como se converte para uma string JSON
    abstract fun toJson(): String
}

// Representa uma string JSON: ex. "olá"
data class JString(val value: String) : JValue() {
    override fun toJson() = "\"${value}\""
}

// Representa um número JSON: ex. 42 ou 3.14
data class JNumber(val value: Number) : JValue() {
    override fun toJson() = value.toString()
}

// Representa um valor booleano JSON: true ou false
data class JBoolean(val value: Boolean) : JValue() {
    override fun toJson() = value.toString()
}

// Representa um valor nulo JSON
object JNull : JValue() {
    override fun toJson() = "null"
}

// Representa um array JSON: ex. [1, "dois", true]
data class JArray(val items: List<JValue>) : JValue() {

    // Converte cada elemento para JSON e junta tudo com vírgulas entre parênteses retos
    override fun toJson(): String =
        items.joinToString(prefix = "[", postfix = "]") { it.toJson() }
}

// Representa um objeto JSON: ex. {"nome": "João", "idade": 25}
data class JObject(val fields: Map<String, JValue>) : JValue() {

    // Converte cada par chave-valor para JSON, separado por vírgulas entre chavetas
    override fun toJson(): String =
        fields.entries.joinToString(prefix = "{", postfix = "}") { (key, value) ->
            "\"$key\": ${value.toJson()}"
        }
}
