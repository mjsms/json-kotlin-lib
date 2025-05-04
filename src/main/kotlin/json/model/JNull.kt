package json.model.elements

import json.visitor.JVisitor

/**
 * Null [JSONElement].
 */
class JNull: JElement() {

    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }
    override fun equals(other: Any?): Boolean = if (other == null) false else other is JNull

}