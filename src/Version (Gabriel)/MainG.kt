//falta fazer as funções do Filter e do map

fun main() {
    // Criar um objeto JSON equivalente a:
    // {
    //   "name": "Gabriel",
    //   "age": 30,
    //   "active": true,
    //   "skills": ["Kotlin", "Java"]
    // }

    val person = JsonObject(
        mapOf(
            "name" to JsonString("Gabriel"),
            "age" to JsonNumber(30),
            "active" to JsonBoolean(true),
            "skills" to JsonArray(
                listOf(
                    JsonString("Kotlin"),
                    JsonString("Java")
                )
            )
        )
    )

    // Serializa o objeto JSON para uma string no formato JSON
    println(person.toJson())
}
