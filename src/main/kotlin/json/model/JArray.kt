package model.elements

/**
 * In-memory representation of a JSON array.
 *
 * The class is _immutable_: its [items] list is a `List` rather than
 * `MutableList`, so once a `JArray` is created the elements cannot be altered
 * in place (functional style).  Use the provided helper functions
 * – `plus`, `filter`, `map`, etc. – to derive new arrays.
 */
data class JArray(val items: List<JValue>) : JValue(), Iterable<JValue> {

    /* ─────────────────────────── Serialization ─────────────────────────── */

    /** Serialises this array to valid JSON, e.g. `[1,"two",false]` */
    override fun toJson(): String =
        items.joinToString(prefix = "[", postfix = "]") { it.toJson() }

    /* ─────────────────────────── Convenience API ───────────────────────── */

    /** Number of elements (`size` for symmetry with Kotlin collections) */
    val size: Int get() = items.size

    /** Access element by index with the `[]` operator */
    operator fun get(index: Int): JValue = items[index]

    /**
     * Functional `plus` – returns **new** `JArray` with [value] appended.
     * (Does not mutate the original, preserving immutability.)
     */
    operator fun plus(value: JValue): JArray =
        JArray(items + value)

    /**
     * Functional `plus` – concatenates two arrays.
     */
    operator fun plus(other: JArray): JArray =
        JArray(items + other.items)

    /** Allow `for (item in array)` iteration. */
    override fun iterator(): Iterator<JValue> = items.iterator()
}
