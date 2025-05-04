package json.model.elements

import json.visitor.JVisitor

/**
 * JSON object.
 * @property properties A list of JSON properties, i.e. key-value pairs.
 */
data class JObject(private val properties: MutableList<JProperty>): JElement() {

    init {
        properties.forEach { it.parent = this }
    }

    /**
     * Returns a [List] of all the [JProperty]s of this object.
     */
    fun getProperties(): List<JProperty> = properties.toList()

    /**
     * Does the object have a [JProperty] with the given [key]?
     * @param key The JSON property key.
     * @return True if any of the object's properties match the given key; False, otherwise.
     */
    fun hasProperty(key: String): Boolean = properties.any { it.key == key }

    /**
     * Add a new [JProperty] to the object if the object has no existing property with the same [key].
     * Otherwise, throws an [IllegalStateException].
     * @param key The [JProperty] key.
     * @param value The [JProperty] value.
     * @throws IllegalStateException If the object already contains a property with the given key.
     */
    fun addProperty(key: String, value: JElement) {
        if (hasProperty(key)) throw IllegalStateException("Property with key $key already exists")
        val property = JProperty(key, value)
        properties.add(property)
        property.parent = this
    }

    /**
     * Returns the [Jelement] value of this object's [key] [JProperty].
     * @param key The [JProperty] key.
     */
    fun getProperty(key: String): JElement = properties.first { it.key == key }.value

    /**
     * Sets a [JProperty] on this object. If the property with the given [key] is not present, it is added.
     * @param key The [JProperty] key.
     * @param value The [JProperty] value.
     */
    fun setProperty(key: String, value: JElement) {
        if (hasProperty(key)) {
            val old = properties[properties.indexOfFirst { it.key == key }]
            val new = JProperty(key, value)
            new.parent = this
            properties[properties.indexOfFirst { it.key == key }] = new
        }
        else addProperty(key, value)
    }

    /**
     * Removes the [JProperty] with the given [key].
     * @param key The [JProperty] key.
     */
    fun removeProperty(key: String) {
        if (!hasProperty(key)) return
        val indexToRemove = properties.indexOfFirst { it.key == key }
        val removed = properties[indexToRemove]
        properties.removeAt(indexToRemove)
    }

    override fun accept(visitor: JVisitor) {
        if (visitor.visit(this))
            properties.forEach { it.accept(visitor) }
    }
}