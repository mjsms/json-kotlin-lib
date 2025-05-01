package json

import kotlin.test.*
import json.model.elements.*
import org.junit.Test

class TestSerialization {

    @Test
    fun stringEscaping() {
        val js = JString("hello \\ \" world")
        assertEquals("\"hello \\\\ \\\" world\"", js.toJson())
    }

    @Test
    fun objectSerialization() {
        val jsonObj = JObject(
            mapOf(
                "name" to JString("Alice"),
                "age" to JNumber(30),
                "active" to JBoolean(true),
                "tags" to JArray(listOf(JString("kotlin"), JString("json")))
            )
        )
        val out = jsonObj.toJson().replace(" ", "")
        assertEquals("{\"name\":\"Alice\",\"age\":30,\"active\":true,\"tags\":[\"kotlin\",\"json\"]}", out)
    }
}