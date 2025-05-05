package json.model.elements

import json.visitor.JVisitor
import json.fn.nodeIdent

/**
 * JSON numeric literal – represents either an integer or a floating‑point
 * value in the JSON tree.
 *
 * @constructor Creates a `JNumber` holding [number].
 * @property number The numeric payload; may be any Kotlin [Number] subtype
 *                  (`Int`, `Long`, `Double`, …).
 */
data class JNumber(val number: Number) : JElement() {

    /**
     * Serialises this `JNumber` to its JSON representation.
     *
     * * Delegates indentation to [nodeIdent] so the output aligns with the
     *   element’s [depth] in pretty‑printed trees.
     * * Uses `Number.toString()` which—for standard numeric types—already
     *   produces JSON‑compatible text (no quotes, lowercase `e` for
     *   scientific notation, etc.).
     */
    override fun toString(): String = nodeIdent(this) + number.toString()

    /**
     * Dispatches this node to the supplied [visitor].
     *
     * Because a number is a leaf node, the visitor is invoked for this element
     * and no further traversal occurs.
     *
     * @param visitor An implementation of [JVisitor] that will process the
     *                current `JNumber`.
     */
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }
}
