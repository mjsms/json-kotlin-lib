package json.model.elements

import json.visitor.JVisitor

/**
 * In‑memory representation of a JSON array.
 *
 * The class is **immutable**: its [elements] list is a read‑only `List`,
 * therefore once a `JArray` is created the contained values cannot be mutated
 * in place. Use helper functions (`plus`, `filter`, `map`, …) to derive new
 * arrays while preserving the original.
 */
data class JArray(private val elements: List<JElement>) : JElement() {

    /* ------------------------------------------------------------------ */
    /*  Initialise parent back‑pointers                                    */
    /* ------------------------------------------------------------------ */

    init {
        // Link every child element back to this array so that utilities
        // relying on `parent` (depth calculation, ancestor lookup, etc.)
        // work correctly.
        elements.forEach { it.parent = this }
    }

    /* ------------------------------------------------------------------ */
    /*  Basic information / helpers                                       */
    /* ------------------------------------------------------------------ */

    /** Number of elements – mirrors Kotlin collection’s `size`. */
    val size: Int
        get() = elements.size

    /** Random access operator (`array[0]`). */
    operator fun get(index: Int): JElement = elements[index]

    /** Immutable copy of the element list. */
    fun getElements(): List<JElement> = elements.toList()

    /* ------------------------------------------------------------------ */
    /*  Serialisation                                                     */
    /* ------------------------------------------------------------------ */

    override fun toString(): String =
        "[\n${elements.joinToString(",\n")}\n${"\t".repeat(depth)}]"

    /* ------------------------------------------------------------------ */
    /*  Visitor dispatch                                                  */
    /* ------------------------------------------------------------------ */

    override fun accept(visitor: JVisitor) {
        if (visitor.visit(this)) {
            elements.forEach { it.accept(visitor) }
        }
    }
}
