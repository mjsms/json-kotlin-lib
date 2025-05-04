package  json.fn

import  json.model.elements.*

/* ───────────────────────────── filter / map ───────────────────────────── */

/**
 * Functional filter for a [JObject].
 * Returns a **new** object that contains only the fields for which
 * [predicate] returns `true`.  The original instance is left untouched.
 */
fun JObject.filter(predicate: (String, JElement) -> Boolean): JObject =
    JObject(
        getProperties()
            .filter { predicate(it.key, it.value) }
            .toMutableList()
    )

/**
 * Functional filter for a [JArray].
 * Produces a **new** array with the elements that satisfy [predicate];
 * does not mutate the original list.
 */
fun JArray.filter(predicate: (JElement) -> Boolean): JArray =
    JArray(elements.filter(predicate))

/**
 * Functional map for a [JArray].
 * Transforms every element with [transform] and returns a **new**
 * `JArray` containing the results.
 */
fun JArray.map(transform: (JElement) -> JElement): JArray =
    JArray(elements.map(transform))

