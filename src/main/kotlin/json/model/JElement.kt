package json.model.elements

import json.visitor.IAcceptVisitors

/**
 * Root of the in‑memory JSON hierarchy.
 *
 * Every concrete JSON node (`JObject`, `JArray`, `JString`, `JNumber`,
 * `JBoolean`, `JNull`, `JProperty`) extends this sealed class so that a visitor
 * can treat them uniformly.
 *
 * @constructor Creates a JSON element with an optional [parent] link.
 *              The link is **internal** because it is managed exclusively by
 *              the model (e.g. a `JObject` sets itself as parent of each
 *              `JProperty`, which then sets itself as parent of its value).
 * @property parent The parent node (object, array, or property) that contains
 *                  this element, or `null` if this element is the root of the
 *                  JSON tree.  Used for upward navigation, depth calculation
 *                  and context‑sensitive validation.
 */
sealed class JElement(internal var parent: JElement? = null) : IAcceptVisitors {

    /**
     * Depth of this element in the tree (root = 0, its children = 1, …).
     *
     * * Starts at the parent’s depth and adds **1**.
     * * If the parent is a [JProperty], subtract **1** because a property is
     *   an auxiliary node; the property’s *value* shares the same visual
     *   indentation as the property itself.
     *
     * This calculation supports pretty‑printing and any algorithms that need
     * level information without storing an explicit depth field in every
     * node.
     */
    internal val depth: Int
        get() = 1 + (parent?.depth ?: -1) + (if (parent is JProperty) -1 else 0)
}
