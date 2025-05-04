package json.model.elements

import json.visitor.JVisitor

/**
 * JSON number.
 * @property number The number value held by this [JElement].
 */
data class JNumber(val number: Number): JElement() {
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }
}