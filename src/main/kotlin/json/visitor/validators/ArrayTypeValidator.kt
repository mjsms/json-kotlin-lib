package json.visitor.validators

import json.model.elements.*
import json.visitor.JVisitor
import kotlin.reflect.KClass

/**
 * Ensures every JArray contains elements of the same (non-null) JValue type.
 *
 * `null` (`JNull`) is ignored when determining the reference type, but if the
 * array contains *both* null and a non-null element, that is still valid.
 *
 * Example failures:
 *   [1, "two"]                   -> mixed JNumber / JString
 *   [true, JNull, JNumber(5)]    -> mixed JBoolean / JNumber
 *
 * Call result() for a list of problems; empty list means all arrays are OK.
 */
class ArrayTypeValidatorVisitor : JVisitor {

    private val problems = mutableListOf<String>()

    override fun visit(value: JArray) {
        // First non-null element determines the expected class
        val firstNonNull = value.items.firstOrNull { it !is JNull } ?: return
        val expected: KClass<out JValue> = firstNonNull::class

        value.items.forEachIndexed { index, elem ->
            if (elem !is JNull && elem::class != expected) {
                problems += "Array has mixed types at index $index: " +
                            "expected $expected but found ${elem::class}"
            }
        }
    }

    fun result(): List<String> = problems.toList()
}
