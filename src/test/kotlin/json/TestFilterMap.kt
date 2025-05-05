package json

import kotlin.test.*
import json.model.elements.*
import json.fn.*
import org.junit.Test

class TestFilterMap {

    @Test
    fun objectFilter() {
        val obj = JObject(
            mutableListOf(
               JProperty("id",JNumber(1)),
                JProperty("name",JString("Bob")),
                JProperty("secret",JString("xxx")),
            )
        )
        val visible = obj.filter { key, _ -> key != "secret" }
        assertFalse(visible.hasProperty("secret"))
        assertEquals(JNumber(1), visible.getProperty("id"))
    }

    @Test
    fun arrayMap() {
        val arr = JArray(listOf(JNumber(1), JNumber(2), JNumber(3)))
        val doubled = arr.map { JNumber((it as JNumber).number.toInt() * 2) }
        assertEquals(JArray(listOf(JNumber(2), JNumber(4), JNumber(6))), doubled)
    }
}