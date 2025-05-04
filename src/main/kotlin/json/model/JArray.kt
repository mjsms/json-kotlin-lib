package json.model.elements

import json.visitor.JVisitor

/**
 * In-memory representation of a JSON array.
 *
 * The class is _immutable_: its [elements] list is a `List` rather than
 * `MutableList`, so once a `JArray` is created the elements cannot be altered
 * in place (functional style).  Use the provided helper functions
 * – `plus`, `filter`, `map`, etc. – to derive new arrays.
 */
data class JArray(val elements: List<JElement>) : JElement() {

    /** Number of elements (`size` for symmetry with Kotlin collections) */
    val size: Int get() = elements.size

    /** Access element by index with the `[]` operator */
    operator fun get(index: Int): JElement = elements[index]

    /**
     * Returns a [List] of all the [JElement]s contained in the array.
     */
    fun getElements(): List<JElement> = elements.toList()


    override fun accept(visitor: JVisitor) {
        if (visitor.visit(this))
            elements.forEach { it.accept(visitor) }
    }
}
