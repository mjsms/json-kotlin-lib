package `Version (Gabriel)`

fun main() {
    // Creating a JSON object like:
    // {
    //   "name": "Gabriel",
    //   "age": 28,
    //   "active": true,
    //   "skills": ["Kotlin", "Java"]
    // }

    val person = JsonObject(
        mapOf(
            "name" to JsonString("Gabriel"),
            "age" to JsonNumber(28),
            "active" to JsonBoolean(true),
            "skills" to JsonArray(
                listOf(
                    JsonString("Kotlin"),
                    JsonString("Java")
                )
            )
        )
    )

    // Serialize the JSON object to a JSON-formatted string
    println(person.toJson())
}
