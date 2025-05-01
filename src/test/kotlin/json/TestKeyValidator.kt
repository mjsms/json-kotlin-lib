package json

import kotlin.test.*
import json.model.elements.*
import json.fn.accept
import json.visitor.validators.KeyValidatorVisitor

class TestKeyValidator {

    @Test
    fun blankKeyFails() {
        val invalid = JObject(mapOf("" to JNumber(1)))
        val validator = KeyValidatorVisitor()
        invalid.accept(validator)
        assertTrue(validator.result().isNotEmpty())
    }
}