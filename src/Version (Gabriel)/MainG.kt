//falta fazer as funções do Filter e do map

fun main() {
    // Criar um objeto JSON equivalente a:
    // {
    //   "name": "Gabriel",
    //   "age": 30,
    //   "active": true,
    //   "skills": ["Kotlin", "Java"]
    // }

    val person = JObject(
        mapOf(
            "name" to JString("Gabriel"),
            "age" to JNumber(30),
            "active" to JBoolean(true),
            "skills" to JArray(
                listOf(
                    JString("Kotlin"),
                    JString("Java")
                )
            )
        )
    )

    // Serializa o objeto JSON para uma string no formato JSON
    println(person.toJson())
}
