package json.model.elements

import json.visitor.JVisitor

/**
 * In‑memory representation of a JSON object.
 *
 * Internally the key–value pairs are stored as a mutable list of [JProperty]
 * to allow efficient updates, but callers interact with the object via an
 * immutable facade (see [getProperties]) or with the convenience modifiers
 * [addProperty], [setProperty] and [removeProperty].
 *
 * @constructor Builds a `JObject` with an initial [properties] list.
 *              Each property’s [parent][JElement.parent] link is assigned to
 *              this object so that upward navigation works.
 * @param properties Mutable list of [JProperty] that make up the object.
 *                   The list is **not** exposed directly; use the provided
 *                   helpers to query or modify its contents.
 */
data class JObject(private val properties: MutableList<JProperty>) : JElement() {

    /* ------------------------------------------------------------------ */
    /*  Construction                                                      */
    /* ------------------------------------------------------------------ */

    init {
        // maintain parent back‑pointers
        properties.forEach { it.parent = this }
    }

    /* ------------------------------------------------------------------ */
    /*  Query helpers                                                     */
    /* ------------------------------------------------------------------ */

    /**
     * Returns an **immutable snapshot** of the object’s properties.
     *
     * @return A new `List` containing all current [JProperty] instances.
     *         Mutating the returned list does **not** affect this object.
     */
    fun getProperties(): List<JProperty> = properties.toList()

    /**
     * Checks whether this object contains a property whose [JProperty.key]
     * equals [key].
     *
     * @param key Property name to search for.
     * @return `true` if such a property exists, `false` otherwise.
     */
    fun hasProperty(key: String): Boolean =
        properties.any { it.key == key }

    /* ------------------------------------------------------------------ */
    /*  Modification helpers                                              */
    /* ------------------------------------------------------------------ */

    /**
     * Retrieves the value associated with the given [key].
     *
     * @param key Name of the property to fetch.
     * @return The [JElement] stored under [key].
     * @throws NoSuchElementException if no property with that key exists.
     */
    fun getProperty(key: String): JElement =
        properties.first { it.key == key }.value

    /**
     * Adds a **new** property `[key] : [value]`.
     *
     * @throws IllegalStateException if a property with the same [key] is
     *                               already present.
     */
    fun addProperty(key: String, value: JElement) {
        require(!hasProperty(key)) { "Property \"$key\" already exists" }
        val prop = JProperty(key, value).also { it.parent = this }
        properties += prop
    }

    /**
     * Replaces the value of an existing property or appends a new one
     * if the key is not present.
     *
     * @param key   The property name.
     * @param value The new value to associate with [key].
     */
    fun setProperty(key: String, value: JElement) {
        val idx = properties.indexOfFirst { it.key == key }
        val newProp = JProperty(key, value).also { it.parent = this }
        if (idx >= 0) {
            properties[idx] = newProp         // update in place
        } else {
            properties += newProp             // add at the end
        }
    }

    /**
     * Removes the property whose key equals [key], if it exists.
     * No action is taken when the key is absent.
     *
     * @param key The property name to remove.
     */
    fun removeProperty(key: String) {
        val idx = properties.indexOfFirst { it.key == key }
        if (idx >= 0) properties.removeAt(idx)
    }

    /* ------------------------------------------------------------------ */
    /*  Serialisation                                                     */
    /* ------------------------------------------------------------------ */

    /**
     * Pretty‑prints this object with tab indentation.
     *
     * Produces `{ }` for an empty object; otherwise prints each property on
     * its own line at the correct depth.
     */
    override fun toString(): String =
        if (properties.isEmpty()) "{ }"
        else {
            val indent = "\t".repeat(depth)
            val inner  = properties.joinToString(",\n")
            if (parent is JProperty)
                "{\n$inner\n$indent}"
            else
                "$indent{\n$inner\n$indent}"
        }

    /* ------------------------------------------------------------------ */
    /*  Visitor dispatch                                                  */
    /* ------------------------------------------------------------------ */

    /**
     * Accepts a [JVisitor] and, if the visitor indicates so, forwards it to
     * all child properties in depth‑first order.
     */
    override fun accept(visitor: JVisitor) {
        if (visitor.visit(this)) {
            properties.forEach { it.accept(visitor) }
        }
    }
}
