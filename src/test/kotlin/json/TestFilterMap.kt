package json

import kotlin.test.*
import json.model.elements.*
import json.fn.*
import org.junit.Test

class TestFilterMap {

    @Test
    fun objectFilter() {
        val obj = JObject(
            mapOf(
                "id" to JNumber(1),
                "name" to JString("Bob"),
                "secret" to JString("xxx")
            )
        )
        val visible = obj.filter { key, _ -> key != "secret" }
        assertNull(visible["secret"])
        assertEquals(JNumber(1), visible["id"])
    }

    @Test
    fun arrayMap() {
        val arr = JArray(listOf(JNumber(1), JNumber(2), JNumber(3)))
        val doubled = arr.map { JNumber((it as JNumber).value.toInt() * 2) }
        assertEquals(JArray(listOf(JNumber(2), JNumber(4), JNumber(6))), doubled)
    }
}