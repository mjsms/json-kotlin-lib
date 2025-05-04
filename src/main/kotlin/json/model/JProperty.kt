package json.model.elements

import json.visitor.JVisitor

/**
 * JSON object property.
 * @property key The name of the property.
 * @property value The value of the property. Can be any [JElement].
 */
data class JProperty(val key: String, val value: JElement): JElement() {
    init {
        value.parent = this
    }

    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
        value.accept(visitor)
    }
}