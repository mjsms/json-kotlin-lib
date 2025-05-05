package json.model.elements

import json.visitor.JVisitor
import json.fn.nodeIdent

/**
 * JSON boolean literal – represents the values **true** or **false**.
 *
 * @constructor Creates a new `JBoolean` holding [boolean].
 * @property boolean The underlying Kotlin `Boolean` value.
 */
data class JBoolean(val boolean: Boolean) : JElement() {

    /**
     * Serialises this boolean to its JSON textual representation.
     *
     * * Delegates indentation to [nodeIdent] so the output aligns with
     *   the node’s [depth] during pretty‑printing.
     * * Uses `Boolean.toString()` which already yields the JSON‑compatible
     *   lowercase `"true"` or `"false"`.
     */
    override fun toString(): String = nodeIdent(this) + boolean.toString()

    /**
     * Accepts a [JVisitor] according to the Composite‑Visitor pattern.
     *
     * Because a boolean is a leaf node, the visitor is invoked for this
     * element only; no further traversal occurs.
     *
     * @param visitor The visitor instance that will process this node.
     */
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }
}
