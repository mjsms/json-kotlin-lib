package json.visitor.validators

import json.model.elements.*
import json.visitor.JVisitor

/**
 * Checks every JObject in the tree for two rules:
 *  1. Keys are not blank ("", "  ", etc.).
 *  2. Keys are unique **inside the same object**.
 *
 * Call result() → list of human-readable error messages.
 * An empty list means the JSON model is valid for those rules.
 */
class KeyValidatorVisitor : JVisitor {

    private val problems = mutableListOf<String>()

    override fun visit(value: JObject) {
        val seen = mutableSetOf<String>()
        value.fields.keys.forEach { key ->
            if (key.isBlank()) {
                problems += "Object contains blank key: $value"
            }
            if (!seen.add(key)) {
                problems += "Duplicate key \"$key\" in object: $value"
            }
        }
    }

    /** Returns an immutable list of problems (empty = no problem). */
    fun result(): List<String> = problems.toList()
}
