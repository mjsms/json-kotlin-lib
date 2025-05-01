package json

import json.model.elements.*
import json.fn.accept
import json.visitor.validators.ArrayTypeValidatorVisitor
import junit.framework.TestCase.assertTrue
import org.junit.Test

class TestArrayValidator {

    @Test
    fun mixedTypesFail() {
        val arr = JArray(listOf(JNumber(1), JString("two")))
        val v = ArrayTypeValidatorVisitor()
        arr.accept(v)
        assertTrue(v.result().isNotEmpty())
    }

    @Test
    fun homogeneousPass() {
        val arr = JArray(listOf(JString("a"), JString("b"), JNull))
        val v = ArrayTypeValidatorVisitor()
        arr.accept(v)
        assertTrue(v.result().isEmpty())
    }
}