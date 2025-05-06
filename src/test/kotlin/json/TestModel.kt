package json

import kotlin.test.*
import json.model.elements.* // assuming you have such a visitor
import org.junit.Test
import json.visitor.*

/* ---------------------------------------------------------------------- */
/*  Sample JSON models                                                    */
/* ---------------------------------------------------------------------- */

/** Valid JSON: a library with two books */
private val validLibrary = JObject(mutableListOf(
    JProperty("library", JString("Downtown Branch")),
    JProperty("open",    JBoolean(true)),
    JProperty("books", JArray(listOf(
        JObject(mutableListOf(
            JProperty("title",  JString("Kotlin in Action")),
            JProperty("author", JString("Dmitry Jemerov")),
            JProperty("pages",  JNumber(360))
        )),
        JObject(mutableListOf(
            JProperty("title",  JString("Clean Architecture")),
            JProperty("author", JString("Robert C. Martin")),
            JProperty("pages",  JNumber(432))
        ))
    )))
))

private val emptyJson = JObject(mutableListOf())

/** Invalid JSON: same array gets a mixed‑type element */
private val invalidLibrary = JObject(mutableListOf(
    JProperty("library", JString("Downtown Branch")),
    JProperty("open",    JBoolean(true)),
    JProperty("books", JArray(listOf(
        JObject(mutableListOf(
            JProperty("title",  JString("Kotlin in Action")),
            JProperty("author", JString("Dmitry Jemerov")),
            JProperty("pages",  JNumber(360))
        )),
        JString("just a string – shouldn’t be in books array")      // ← mixed type
    )))
))

/* ---------------------------------------------------------------------- */
/*  Tests                                                                 */
/* ---------------------------------------------------------------------- */
private fun String.clean(): String =
    replace('\u00A0', ' ')        // NBSP → space
        .replace("\\s".toRegex(), "") // strip all whitespace
class JsonPrintingTest {

    @Test
    fun `toString pretty prints`() {
        val expected = """
            {
                "library": "Downtown Branch",
                "open": true,
                "books": [
                    {
                        "title": "Kotlin in Action",
                        "author": "Dmitry Jemerov",
                        "pages": 360
                    },
                    {
                        "title": "Clean Architecture",
                        "author": "Robert C. Martin",
                        "pages": 432
                    }
                ]
            }
        """.trimIndent()
        assertEquals(expected.clean(), validLibrary.toString().clean())

        assertEquals("{ }", JObject(mutableListOf()).toString())
    }
}

class ObjectManipulationTest {

    @Test
    fun `add set remove property`() {
        val obj = JObject(mutableListOf())

        assertFalse(obj.hasProperty("isbn"))

        obj.addProperty("isbn", JString("978-0134494166"))
        assertEquals(JString("978-0134494166"), obj.getProperty("isbn"))

        obj.setProperty("isbn", JString("978-1617293290"))
        assertEquals(JString("978-1617293290"), obj.getProperty("isbn"))

        obj.removeProperty("isbn")
        assertFalse(obj.hasProperty("isbn"))
    }
}

class ArrayManipulationTest {

    @Test
    fun `mutate array`() {
        val arr = JArray(listOf())
        assertEquals(0, arr.size)

        val updated = JArray(arr.getElements() + JNumber(1))
        assertEquals(1, updated.size)
        assertEquals(JNumber(1), updated[0])
    }
}


class VisitorTests {

    @Test
    fun `collect names by key`() {
        val collector = CollectByKey("author")
        validLibrary.accept(collector)
        val names = collector.collected.map { (it as JString).string }
        assertEquals(listOf("Dmitry Jemerov", "Robert C. Martin"), names)
    }

    @Test
    fun `JSON Visitors`() {
        val collector = CollectByKey("name")
        validLibrary.accept(collector)
        assertEquals(listOf("\"Sapo\"", "\"Joaquim\"", "\"Larapo\""), collector.collected.map { it.toString() })

        val collector2 = CollectByKey("ThisKeyDoesNotExistAtAllAndItsSoLongThatItsGettingSilly")
        emptyJson.accept(collector2)
        assertEquals(listOf(), collector2.collected.map { it.toString() })

        val validator = ArrayTypeValidator()
        validLibrary.accept(validator)
        assertTrue(validator.valid)

        val validator2 = ArrayTypeValidator()
        emptyJson.accept(validator2)
        assertTrue(validator2.valid)

        val invalidator = ArrayTypeValidator()
        invalidLibrary.accept(invalidator)
        assertFalse(invalidator.valid)
    }
}
