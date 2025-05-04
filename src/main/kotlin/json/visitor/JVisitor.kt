package json.visitor

import json.model.elements.*

/**
 * Generic interface for any objects that accept JSON visitors.
 */
interface IAcceptVisitors {
    fun accept(visitor: JVisitor)
}

/**
 * Base interface for the Visitor pattern over the JSON composite.
 *
 * * Primitive visit methods (`JString`, `JNumber`, `JBoolean`, `JNull`)
 *   have default no-op implementations so concrete visitors can override
 *   only what they actually need.
 * * Composite types (`JArray`, `JObject`) are left abstract because every
 *   useful visitor will generally need to handle them.
 */
interface JVisitor {
    fun visit(obj: JObject): Boolean = true
    fun visit(array: JArray): Boolean = true
    fun visit(property: JProperty) {}
    fun visit(string: JString) {}
    fun visit(number: JNumber) {}
    fun visit(boolean: JBoolean) {}
    fun visit(empty: JNull) {}
}
/**
 * Example visitor: collects all values of every [JSONProperty] with the specific [key].
 * @property key The [JSONProperty] key.
 */
class CollectByKey(private val key: String) : JVisitor {
    val collected: MutableList<JElement> = mutableListOf()

    override fun visit(property: JProperty) {
        if (property.key == key)
            collected.add(property.value)
    }

    override fun visit(array: JArray): Boolean = true

    override fun visit(obj: JObject): Boolean = true
}

/**
 * Example visitor: validates that all [JSONArray]s in the [JSONObject] are type-uniform.
 */
class ArrayTypeValidator : JVisitor {
    var valid: Boolean = true

    override fun visit(array: JArray): Boolean {
        valid = array.getElements().map { it::class }.toSet().size == 1
        return true
    }

    override fun visit(obj: JObject): Boolean = true
}