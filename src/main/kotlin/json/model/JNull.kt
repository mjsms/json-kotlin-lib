package json.model.elements

import json.visitor.JVisitor
import json.fn.nodeIdent

/**
 * JSON null literal – represents the value **null** in the JSON model.
 *
 * Although many libraries model this as a Kotlin `object` singleton,
 * here it is a regular `class` to match the style of the other
 * `JElement` subclasses.
 */
data object JNull : JElement() {

    /**
     * Serialises this node to the JSON text `"null"`, preceded by the
     * indentation prefix returned from [nodeIdent] so it aligns with
     * the element’s [depth] when pretty‑printed.
     */
    override fun toString(): String = nodeIdent(this) + "null"

    /**
     * Dispatches this node to the supplied [visitor].
     * Being a leaf, the visitor is invoked only for this element.
     *
     * @param visitor The [JVisitor] instance handling traversal logic.
     */
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
    }

}
