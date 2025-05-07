package test.kotlin.json

import json.emptyJson
import json.invalidLibrary
import json.model.elements.*
import json.validLibrary
import json.visitor.ArrayTypeValidator
import json.visitor.KeyValidatorVisitor
import junit.framework.TestCase.*
import org.junit.Test

class KeyValidatorVisitorTest {

    /** `KeyValidatorVisitor` flags blank or duplicate keys within objects. */
    @Test
    fun `key validator`() {
        val badObj = JObject(mutableListOf(
            JProperty("",  JNumber(1)),      // blank key
            JProperty("k", JNumber(2)),
            JProperty("k", JNumber(3))       // duplicate key
        ))

        val validator = KeyValidatorVisitor()
        badObj.accept(validator)

        /* We expect at least two errors (blank + duplicate). */
        assertTrue(validator.result().size >= 2)

        /* Same visitor on a clean object must yield no errors. */
        val goodObj = JObject(mutableListOf(
            JProperty("a", JBoolean(true)),
            JProperty("b", JNull())
        ))
        val v2 = KeyValidatorVisitor()
        goodObj.accept(v2)
        assertTrue(v2.result().isEmpty())
    }

    /** `ArrayTypeValidator` must accept homogeneous arrays and reject mixed. */
    @Test
    fun `array type validator`() {
        val ok = ArrayTypeValidator()
        validLibrary.accept(ok)
        assertTrue(ok.valid)

        val alsoOk = ArrayTypeValidator()
        emptyJson.accept(alsoOk)          // no arrays → still valid
        assertTrue(alsoOk.valid)

        val bad = ArrayTypeValidator()
        invalidLibrary.accept(bad)        // mixed types encountered
        assertFalse(bad.valid)
    }
}