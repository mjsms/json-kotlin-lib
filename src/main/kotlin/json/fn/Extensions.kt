package model.fn

import model.elements.*

/* ───────────────────────────── filter / map ───────────────────────────── */

/**
 * Functional filter for a [JObject].
 * Returns a **new** object that contains only the fields for which
 * [predicate] returns `true`.  The original instance is left untouched.
 */
fun JObject.filter(predicate: (key: String, value: JValue) -> Boolean): JObject =
    JObject(fields.filter { (k, v) -> predicate(k, v) })

/**
 * Functional filter for a [JArray].
 * Produces a **new** array with the elements that satisfy [predicate];
 * does not mutate the original list.
 */
fun JArray.filter(predicate: (JValue) -> Boolean): JArray =
    JArray(items.filter(predicate))

/**
 * Functional map for a [JArray].
 * Transforms every element with [transform] and returns a **new**
 * `JArray` containing the results.
 */
fun JArray.map(transform: (JValue) -> JValue): JArray =
    JArray(items.map(transform))

/* ───────────────────────────── Visitor bridge ──────────────────────────── */

/**
 * Base interface for the Visitor pattern.
 * You can extend this to implement validation, pretty-printing, searching,
 * or any other traversal-based feature.
 *
 * Primitive visit methods have default no-op implementations so that
 * a concrete visitor may override only what it needs.
 */
interface JVisitor {
    fun visit(value: JString)  { /* no-op */ }
    fun visit(value: JNumber)  { /* no-op */ }
    fun visit(value: JBoolean) { /* no-op */ }
    fun visit(value: JNull)    { /* no-op */ }
    fun visit(value: JArray)
    fun visit(value: JObject)
}

/**
 * Extension function that enables `jsonValue.accept(visitor)` on *any*
 * [JValue].  It performs the recursive descent so individual `JValue`
 * subclasses remain free of traversal code.
 */
fun JValue.accept(visitor: JVisitor) {
    when (this) {
        is JString  -> visitor.visit(this)
        is JNumber  -> visitor.visit(this)
        is JBoolean -> visitor.visit(this)
        is JNull    -> visitor.visit(this)

        is JArray   -> {
            visitor.visit(this)
            items.forEach { it.accept(visitor) }   // depth-first traversal
        }

        is JObject  -> {
            visitor.visit(this)
            fields.values.forEach { it.accept(visitor) }
        }
    }
}
