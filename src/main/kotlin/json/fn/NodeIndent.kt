package json.fn

import json.model.elements.JElement
import json.model.elements.JProperty


/**
 * Computes the indentation prefix used when pretty‑printing a [JElement].
 *
 * * If the element is the **value part** of a [JProperty] its parent already prints
 *   the indentation and the `"key":` label, so this function returns an **empty
 *   string**.
 * * For every other node it returns a sequence of `\t` (tab) characters whose
 *   length equals the element’s [JElement.depth], aligning the output with its
 *   logical level in the JSON tree.
 *
 * @return A string containing zero or more tab characters that should be
 *         prepended to the element’s textual representation.
 */
internal val nodeIdent: (JElement) -> String = { e ->
    if (e.parent is JProperty) "" else "\t".repeat(e.depth)
}