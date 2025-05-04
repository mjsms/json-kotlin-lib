package json.model.elements

import json.visitor.JVisitor

/**
 * JSON boolean.
 * @property boolean The boolean value held by this [JElement].
 */
data class JBoolean(val boolean: Boolean): JElement() {
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }
}