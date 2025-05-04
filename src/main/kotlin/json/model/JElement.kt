package json.model.elements

import json.visitor.IAcceptVisitors

/**
 * Generic JSON element in the in‑memory tree.
 *
 * @property parent The parent node (object, array, or property) that contains
 *                  this element, or `null` if this element is the root.
 */
sealed class JElement(internal var parent: JElement? = null) : IAcceptVisitors{
    internal val depth: Int
        get() = 1 + (parent?.depth ?: -1) + (if (parent is JProperty) -1 else 0)
}

