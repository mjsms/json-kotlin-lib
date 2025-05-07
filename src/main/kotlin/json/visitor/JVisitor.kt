package json.visitor

import json.model.elements.*

/* ---------------------------------------------------------------------- */
/*  Accepting side of the Visitor pattern                                 */
/* ---------------------------------------------------------------------- */

/**
 * Contract for any JSON AST node that can be traversed by a [JVisitor].
 *
 * Concrete subclasses of [json.model.elements.JElement] implement `accept`
 * by calling one of the visitor’s `visit(…)` methods and, where appropriate,
 * forwarding the visitor to their children.
 */
interface IAcceptVisitors {

    /**
     * Dispatches this node to [visitor] according to the Composite–Visitor
     * pattern.
     *
     * Implementations should:
     * 1. Call the matching `visitor.visit(this)` overload.
     * 2. If that call returns `true`, propagate the visitor to their direct
     *    children in depth‑first order.
     */
    fun accept(visitor: JVisitor)
}

/* ---------------------------------------------------------------------- */
/*  Visitor interface                                                     */
/* ---------------------------------------------------------------------- */

/**
 * Base interface for operations that need to traverse the JSON tree.
 *
 * ### Conventions
 * * The `visit(JObject)` and `visit(JArray)` functions return **`Boolean`**:
 *   * `true` → the visitor wishes to descend into the node’s children.
 *   * `false` → skip the subtree (useful for filtering).
 * * Primitive visit methods (`JString`, `JNumber`, …) provide default no‑ops
 *   so concrete visitors can override only what they need.
 */
interface JVisitor {

    /* Composite nodes --------------------------------------------------- */

    /** Called for every [JObject]. Return `true` to visit its properties. */
    fun visit(obj: JObject): Boolean = true

    /** Called for every [JArray]. Return `true` to visit its elements. */
    fun visit(array: JArray): Boolean = true

    /** Called for each key–value pair inside an object. */
    fun visit(property: JProperty) {}

    /* Primitive nodes --------------------------------------------------- */

    /** Called for each JSON string literal. */
    fun visit(string: JString) {}

    /** Called for each numeric literal. */
    fun visit(number: JNumber) {}

    /** Called for each boolean literal. */
    fun visit(boolean: JBoolean) {}

    /** Called for the `null` literal. */
    fun visit(empty: JNull) {}
}

/* ---------------------------------------------------------------------- */
/*  Concrete visitors – examples                                          */
/* ---------------------------------------------------------------------- */

/**
 * Validates that **every** [JArray] encountered contains elements
 * of a single non‑null type (homogeneous arrays).
 *
 * * `JNull` elements are ignored when determining the reference type.
 * * The check is cumulative: once [valid] becomes `false` it stays false.
 *
 * @property valid `true` after traversal *only if* all arrays were uniform.
 */
class ArrayTypeValidator : JVisitor {

    /** Becomes `false` as soon as a mixed‑type array is found. */
    var valid: Boolean = true
        private set

    override fun visit(array: JArray): Boolean {
        if (valid) {                                 // skip work once invalid
            val kinds = array.getElements()
                .filterNot { it is JNull }
                .map { it::class }
                .toSet()
            valid = kinds.size <= 1
        }
        return true
    }

    /* Objects don’t affect this validation, so just continue traversal. */
    override fun visit(obj: JObject): Boolean = true
}

/**
 * Visitor that **validates keys inside every [JObject]** it traverses.
 *
 * Rules enforced:
 * 1. **Non‑blank keys** – `""` or whitespace‑only keys are reported.
 * 2. **Uniqueness within the same object** – duplicate keys inside a single
 *    `JObject` are reported (independent objects may share key names).
 *
 * The visitor accumulates human‑readable error messages; call [result] after
 * traversal.  An empty list means the entire JSON tree is valid for these rules.
 */
class KeyValidatorVisitor : JVisitor {

    /** Collected validation errors (empty ⇒ no problems). */
    private val errors = mutableListOf<String>()

    /**
     * Checks the keys of [obj].
     * Always returns `true` so that traversal continues into child nodes.
     */
    override fun visit(obj: JObject): Boolean {
        val seen = mutableSetOf<String>()
        obj.getProperties().forEach { prop ->
            if (prop.key.isBlank()) {
                errors += "Blank key in object at depth ${obj.depth}"
            }
            if (!seen.add(prop.key)) {
                errors += "Duplicate key '${prop.key}' in object at depth ${obj.depth}"
            }
        }
        return true
    }

    /** Immutable view of the problems found. */
    fun result(): List<String> = errors.toList()
}