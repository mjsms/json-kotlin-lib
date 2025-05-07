package json

import json.model.elements.*
import json.visitor.ArrayTypeValidator
import json.visitor.KeyValidatorVisitor
import junit.framework.TestCase.*   // still using JUnit‑4 runner
import org.junit.Test

/* Utility that strips all whitespace so pretty‑print comparisons don’t fail
   due to tabs/spaces or NBSP versus ASCII space. */
private fun String.clean(): String =
    replace('\u00A0', ' ')          // NBSP → space
        .replace("\\s".toRegex(), "") // remove all whitespace

/* ====================================================================== */
/*  Pretty‑printing                                                      */
/* ====================================================================== */

class JsonPrintingTest {

    /** Ensures `toString()` produces valid JSON text (whitespace‑agnostic). */
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

        /* Empty object should render as `{ }` (one space for readability). */
        assertEquals("{ }", JObject(mutableListOf()).toString())
    }
}

/* ====================================================================== */
/*  JObject manipulation helpers                                         */
/* ====================================================================== */

class ObjectManipulationTest {

    /** Checks `addProperty`, `setProperty`, `removeProperty`, `hasProperty`. */
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

/* ====================================================================== */
/*  JArray helpers                                                        */
/* ====================================================================== */

class ArrayManipulationTest {

    /** Verifies `size`, index operator, and building a new array via copy. */
    @Test
    fun `mutate array`() {
        val arr = JArray(listOf())
        assertEquals(0, arr.size)

        val updated = JArray(arr.getElements() + JNumber(1))
        assertEquals(1, updated.size)
        assertEquals(JNumber(1), updated[0])
    }
}
