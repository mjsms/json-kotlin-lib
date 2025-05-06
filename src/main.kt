import main.kotlin.api.annotations.Mapping
import main.kotlin.api.annotations.Param
import main.kotlin.api.annotations.Path
import main.kotlin.api.server.GetJson

fun main() {
    println("Iniciando servidor com os seguintes controllers:")
    println("1. UserController endpoints:")
    UserController::class.java.declaredMethods.forEach {
        println("- ${it.annotations.filterIsInstance<Mapping>().firstOrNull()?.path}")
    }

    println("\n2. ProductController endpoints:")
    ProductController::class.java.declaredMethods.forEach {
        println("- ${it.annotations.filterIsInstance<Mapping>().firstOrNull()?.path}")
    }

    val app = GetJson(UserController(), ProductController())
    app.start(8080)
}

@Mapping("health")
fun healthCheck(): String = "Servidor está funcionando!"

@Mapping("api/users")
class UserController {
    @Mapping("list")
    fun listUsers(): List<User> = listOf(
        User(1, "Alice"),
        User(2, "Bob")
    )

    @Mapping("get/(id)")
    fun getUser(@Path id: Int): User {
        return User(id, "User $id")
    }

    @Mapping("search")
    fun searchUsers(@Param query: String): List<User> {
        return listOf(
            User(1, "Alice"),
            User(2, "Bob")
        ).filter { it.name.contains(query, ignoreCase = true) }
    }
}

@Mapping("api/products")
class ProductController {
    @Mapping("all")
    fun getAllProducts(): Map<String, Any> {
        return mapOf(
            "products" to listOf(
                mapOf("id" to 1, "name" to "Laptop"),
                mapOf("id" to 2, "name" to "Phone")
            ),
            "count" to 2
        )
    }
}

data class User(val id: Int, val name: String)