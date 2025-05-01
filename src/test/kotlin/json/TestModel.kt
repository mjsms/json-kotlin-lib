package json

import kotlin.test.*
import json.model.elements.*

class TestModel {

    @Test
    fun createPrimitives() {
        val s = JString("hello")
        val n = JNumber(42)
        val b = JBoolean(true)
        val nl = JNull

        assertEquals(""hello"", s.toJson())
        assertEquals("42", n.toJson())
        assertEquals("true", b.toJson())
        assertEquals("null", nl.toJson())
    }

    @Test
    fun createComposite() {
        val arr = JArray(listOf(JNumber(1), JNumber(2)))
        val obj = JObject(mapOf("a" to arr, "b" to JBoolean(false)))

        assertEquals("[1,2]", arr.toJson())
        assertTrue(obj.toJson().contains(""a":"))
        assertTrue(obj.toJson().contains(""b": false"))
    }
}