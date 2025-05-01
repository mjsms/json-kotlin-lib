package json.visitor

import json.model.elements.*

/**
 * Base interface for the Visitor pattern over the JSON composite.
 *
 * * Primitive visit methods (`JString`, `JNumber`, `JBoolean`, `JNull`)
 *   have default no-op implementations so concrete visitors can override
 *   only what they actually need.
 * * Composite types (`JArray`, `JObject`) are left abstract because every
 *   useful visitor will generally need to handle them.
 *
 * Add implementations in `json.visitor.validators` (or any sub-package) to
 * perform validation, pretty-printing, searching, etc.
 */
interface JVisitor {

    /* ────── primitives (default no-ops) ────── */

    /** Called for each [JString] node. */
    fun visit(value: JString)  { /* no-op */ }

    /** Called for each [JNumber] node. */
    fun visit(value: JNumber)  { /* no-op */ }

    /** Called for each [JBoolean] node. */
    fun visit(value: JBoolean) { /* no-op */ }

    /** Called for each [JNull] node. */
    fun visit(value: JNull)    { /* no-op */ }

    /* ────── composites (must be implemented) ────── */

    fun visit(value: JArray)   { /* no-op by default */ }
    fun visit(value: JObject)  { /* no-op by default */ }
}
