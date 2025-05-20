package json

import main.kotlin.api.annotations.Mapping
import main.kotlin.api.annotations.Param
import main.kotlin.api.annotations.Path
import main.kotlin.api.server.GetJson



/**
 * startServer
 *
 * Creates an instance of [GetJson] and registers multiple controllers:
 * [UserController], [ProductController], and [GenericController].
 * Starts the HTTP server on port 8080.
 */
fun startServer() {
    val app = GetJson(UserController(), ProductController(), GenericController())
    app.start(8080)
}


/**
 * Main entry point of http the application.
 */
fun main() {
    startServer()
}

/**
 * Global health check endpoint (not tied to a class).
 *
 * Accessible via `/health`, returns a simple status message.
 *
 * @return A string indicating the server is running.
 */
@Mapping("health")
fun healthCheck(): String = "Servidor está a funcionar!"

/**
 * Controller responsible for user-related endpoints.
 *
 * Base path: `/api/users`
 */
@Mapping("api/users")
class UserController {

    /**
     * Lists all users.
     * Accessible via: `/api/users/list`
     *
     * @return A list of [User]s.
     */
    @Mapping("list")
    fun listUsers(): List<User> = listOf(
        User(1, "Alice"),
        User(2, "Bob")
    )

    /**
     * Retrieves a user by their ID.
     * Accessible via: `/api/users/get/{id}`
     *
     * @param id The user ID extracted from the path.
     * @return A [User] with the given ID.
     */
    @Mapping("get/{id}")
    fun getUser(@Path id: Int): User {
        return User(id, "User $id")
    }

    /**
     * Searches users by name using a query parameter.
     * Accessible via: `/api/users/search?query=<text>`
     *
     * @param query The search string.
     * @return A list of users whose name contains the query.
     */
    @Mapping("search")
    fun searchUsers(@Param query: String): List<User> {
        return listOf(
            User(1, "Alice"),
            User(2, "Bob")
        ).filter { it.name.contains(query, ignoreCase = true) }
    }
}

/**
 * Controller for product-related endpoints.
 *
 * Base path: `/api/products`
 */
@Mapping("api/products")
class ProductController {

    /**
     * Retrieves all available products.
     * Accessible via: `/api/products/all`
     *
     * @return A map containing the product list and total count.
     */
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

/**
 * Data class representing a user object.
 *
 * @property id The unique identifier.
 * @property name The user's name.
 */
data class User(val id: Int, val name: String)

/**
 * Generic controller to demonstrate different argument types and return formats.
 *
 * Base path: `/api/generic`
 */
@Mapping("api/generic")
class GenericController {

    /**
     * Returns a demo list of integers.
     * Accessible via: `/api/generic/ints`
     *
     * @return A list of integers.
     */
    @Mapping("ints")
    fun demo(): List<Int> = listOf(1, 2, 3)

    /**
     * Returns a hardcoded key-value pair.
     * Accessible via: `/api/generic/pair`
     *
     * @return A pair of strings.
     */
    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    /**
     * Echoes a path variable with an exclamation mark.
     * Accessible via: `/api/generic/path/{pathvar}`
     *
     * @param pathvar The variable extracted from the path.
     * @return The value of `pathvar` with an exclamation mark.
     */
    @Mapping("path/{pathvar}")
    fun path(
        @Path pathvar: String
    ): String = pathvar + "!"

    /**
     * Demonstrates use of multiple query parameters.
     * Accessible via: `/api/generic/args?n=3&text=hi`
     *
     * @param n Number of repetitions.
     * @param text Text to repeat.
     * @return A map where the key and value are the repeated text.
     */
    @Mapping("args")
    fun args(
        @Param n: Int,
        @Param text: String
    ): Map<String, String> = mapOf(text to text.repeat(n))
}
