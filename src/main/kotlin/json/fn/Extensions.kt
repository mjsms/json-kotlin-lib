package json.fn

import json.model.elements.*

/* ───────────────────────────── filter / map ───────────────────────────── */

/**
 * Returns a **new** [JObject] that contains only the properties for which
 * [predicate] returns `true`.  The original object remains unchanged.
 *
 * We clone each matching [JProperty] so the original children keep their
 * `parent` link and the copy gets its own.
 */
fun JObject.filter(
    predicate: (key: String, value: JElement) -> Boolean
): JObject {
    val clonedProps = getProperties()
        .filter { predicate(it.key, it.value) }
        .map { JProperty(it.key, it.value) }        // fresh instances
        .toMutableList()

    val newObj = JObject(clonedProps)
    clonedProps.forEach { it.parent = newObj }      // set parent links
    return newObj
}

/**
 * Functional filter for a [JArray] that produces a new array.
 * Parent links for retained elements are updated to the new array.
 */
fun JArray.filter(predicate: (JElement) -> Boolean): JArray {
    val kept = getElements().filter(predicate)
    val newArr = JArray(kept)
    kept.forEach { it.parent = newArr }
    return newArr
}

/**
 * Functional map for a [JArray].
 * Applies [transform] to each element, returning a new `JArray`.
 * Parent links for the transformed elements are set to the new array.
 */
fun JArray.map(transform: (JElement) -> JElement): JArray {
    val mapped = getElements().map(transform)
    val newArr = JArray(mapped)
    mapped.forEach { it.parent = newArr }
    return newArr
}
