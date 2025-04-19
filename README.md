
# json-kotlin-lib

A lightweight Kotlin library for generating, manipulating, and serializing JSON data **in-memory** without using any external dependencies (except JUnit for testing).

This library was developed as part of an Advanced Programming course project. It integrates several software design patterns such as **Composite**, **Visitor**, **Functional API**, **Facade**, **Reflection**, **Annotations**, **Decorator**, and **Observer**.

---

## 🚀 Features

- ✅ Programmatic creation of JSON objects and arrays
- ✅ In-memory model for JSON values (`objects`, `arrays`, `strings`, `numbers`, `booleans`, `null`)
- ✅ Filter and map operations on `JsonObject` and `JsonArray`
- ✅ Visitor pattern to validate JSON structure:
  - Unique object keys
  - Uniform types in arrays
- ✅ String serialization into valid JSON
- ✅ Reflection-based conversion from Kotlin objects
- ✅ Annotation support for custom behavior:
  - `@JsonIgnore`, `@JsonName`, `@JsonStringify`
- ✅ Observable JSON elements with listeners for changes

---

## 📦 Installation

Clone the repository and include it in your Kotlin project. A release JAR will be available under the [Releases](https://github.com/your-username/json-kotlin-lib/releases) tab (replace with real link).

---

## 🧪 Running Tests

The project uses **JUnit** for testing.

```bash
./gradlew test
```

---

## 📚 Usage Example

### Creating JSON manually:

```kotlin
val json = Json.obj(
    "name" to "Joana",
    "age" to 25,
    "tags" to Json.arr("gatos", "fotografia")
)
println(json.toJsonString())
// Output: {"name":"Joana","age":25,"tags":["gatos","fotografia"]}
```

### Filtering and Mapping:

```kotlin
val filtered = json.filter { key, _ -> key != "age" }
val upperTags = (json["tags"] as JsonArray).map {
    JsonString((it as JsonString).value.uppercase())
}
```

### Using Reflection:

```kotlin
data class Person(val name: String, val age: Int)
val p = Person("Joana", 25)
val json = Json.from(p)
println(json.toJsonString())
```

---

## 🛠️ Design Patterns Used

- **Composite**: Unified hierarchy of JSON elements
- **Visitor**: Structure validation and traversals
- **Functional API**: Filter/map operations
- **Facade**: Simplified creation API (`Json.obj`, `Json.arr`)
- **Reflection**: Convert Kotlin objects to JSON
- **Annotations**: Customize serialization (`@JsonIgnore`, etc.)
- **Decorator**: Optional metadata layers
- **Observer**: Listen for changes in JSON structures

---

## 📄 License

This project is intended for academic use and is not licensed for production.

---
