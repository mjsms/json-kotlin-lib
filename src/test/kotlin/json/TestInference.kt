import kotlin.test.*
import json.model.elements.*
import json.fn.toJson
import org.junit.Test

enum class Grade { A, B, C }

data class Student(
    val name: String,
    val age: Int,
    val passed: Boolean,
    val grade: Grade?,
)

data class Course(
    val title: String,
    val credits: Int,
    val students: List<Student>,
)

class InferenceTest {

    @Test
    fun `inference of primitive types`() {
        assertEquals(JString("hello"), toJson("hello"))
        assertEquals(JNumber(42), toJson(42))
        assertEquals(JNumber(3.14), toJson(3.14))
        assertEquals(JBoolean(true), toJson(true))
        assertEquals(JNull(), toJson(null))
    }

    @Test
    fun `inference of enum`() {
        assertEquals(JString("A"), toJson(Grade.A))
    }

    @Test
    fun `inference of list`() {
        val result = toJson(listOf(1, 2, 3)) as JArray
        assertEquals(listOf(JNumber(1), JNumber(2), JNumber(3)), result.getElements())
    }

    @Test
    fun `inference of map`() {
        val result = toJson(mapOf("a" to true, "b" to 1)) as JObject
        assertTrue(result.hasProperty("a"))
        assertEquals(JBoolean(true), result.getProperty("a"))
        assertEquals(JNumber(1), result.getProperty("b"))
    }

    @Test
    fun `inference of data class with nested values`() {
        val course = Course(
            "PA", 6, listOf(
                Student("Alice", 21, true, null),
                Student("Bob", 22, false, Grade.B)
            )
        )

        val json = toJson(course).toString()

        assertTrue(json.contains(""""title": "PA""""))
        assertTrue(json.contains(""""name": "Alice""""))
        assertTrue(json.contains(""""grade": null"""))
        assertTrue(json.contains(""""grade": "B""""))
    }
}
