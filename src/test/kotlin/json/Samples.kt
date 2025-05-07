package json

import json.model.elements.*


/* ---------------------------------------------------------------------- */
/*  Sample JSON models used across tests                                  */
/* ---------------------------------------------------------------------- */

/** Valid JSON: a library object with a homogeneous books array. */
val validLibrary = JObject(mutableListOf(
    JProperty("library", JString("Downtown Branch")),
    JProperty("open",    JBoolean(true)),
    JProperty("books", JArray(listOf(
        JObject(mutableListOf(
            JProperty("title",  JString("Kotlin in Action")),
            JProperty("author", JString("Dmitry Jemerov")),
            JProperty("pages",  JNumber(360))
        )),
        JObject(mutableListOf(
            JProperty("title",  JString("Clean Architecture")),
            JProperty("author", JString("Robert C. Martin")),
            JProperty("pages",  JNumber(432))
        ))
    )))
))

/** Empty object to act as a minimal control sample. */
val emptyJson = JObject(mutableListOf())

/** Invalid JSON:books array mixes object and string, violating homogeneity. */
val invalidLibrary = JObject(mutableListOf(
    JProperty("library", JString("Downtown Branch")),
    JProperty("open",    JBoolean(true)),
    JProperty("books", JArray(listOf(
        JObject(mutableListOf(
            JProperty("title",  JString("Kotlin in Action")),
            JProperty("author", JString("Dmitry Jemerov")),
            JProperty("pages",  JNumber(360))
        )),
        JString("just a string – shouldn’t be in books array")
    )))
))
