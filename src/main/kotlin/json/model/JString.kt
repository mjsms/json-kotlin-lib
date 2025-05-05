package json.model.elements

import json.visitor.JVisitor
import json.fn.nodeIdent

/**
 * JSON string – a primitive value that holds a UTF‑8 text sequence.
 *
 * @constructor Creates a new [JString] whose JSON textual value is [string].
 * @property string The raw string payload (without surrounding quotes).
 */
data class JString(val string: String) : JElement() {

    /**
     * Serialises this `JString` to its JSON representation.
     *
     * The implementation delegates indentation to [nodeIdent] and then wraps
     * the contents in double quotes, escaping nothing here because the string
     * is assumed to be pre‑escaped at construction time.
     *
     * @return A compact JSON‑compatible string, e.g. `"\t\"hello\""` when
     *         printed at depth 1.
     */
    override fun toString(): String = nodeIdent(this) + "\"$string\""

    /**
     * Dispatches this node to the supplied [visitor].
     *
     * Because a string has no children, the visitor is simply invoked for
     * this element and no further traversal occurs.
     *
     * @param visitor An implementation of [JVisitor] that will process the
     *                current `JString`.
     */
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }
}
