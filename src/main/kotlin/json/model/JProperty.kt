package json.model.elements

import json.visitor.JVisitor

/**
 * One *key–value pair* inside a JSON object.
 *
 * A `JProperty` is itself a [JElement] so that visitors can treat it
 * uniformly with other node types in the tree.
 *
 * @constructor Creates a property with [key] and [value].
 * @property key   The property name (must be unique within its enclosing object).
 * @property value The property value – can be any concrete subclass of [JElement]
 *                 (object, array, string, number, boolean or null).
 */
data class JProperty(val key: String, val value: JElement) : JElement() {

    // --------------------------------------------------------------------
    //  Initialisation
    // --------------------------------------------------------------------

    init {
        /* Establish the upward link so `value.parent` points back to this
           property; required for depth calculations, ancestor queries and
           visitor logic that relies on parent context. */
        value.parent = this
    }

    /** Pretty JSON like `"title": "Kotlin in Action"` with correct indent. */
    override fun toString(): String =
        "\t".repeat(depth) + "\"$key\": $value"

    // --------------------------------------------------------------------
    //  Visitor dispatch
    // --------------------------------------------------------------------

    /**
     * Accepts a [JVisitor] according to the Composite‑Visitor pattern.
     *
     * * First calls `visitor.visit(this)` so the visitor can act on the
     *   property node itself (e.g. check the key, record the path, …).
     * * Then propagates the same visitor to the enclosed [value], enabling a
     *   complete depth‑first traversal of the JSON tree.
     *
     * @param visitor An implementation of [JVisitor] provided by the caller.
     */
    override fun accept(visitor: JVisitor) {
        visitor.visit(this)
        value.accept(visitor)
    }
}
