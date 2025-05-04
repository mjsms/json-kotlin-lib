package json.model.elements

import json.visitor.JVisitor

/**
 * JSON string.
 * @property string The string held by this [JElement].
 */
data class JString(val string: String): JElement() {
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }
}