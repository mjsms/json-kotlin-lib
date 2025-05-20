package main.kotlin.api.server

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

/**
 * Lightweight HTTP server launcher for serving JSON responses.
 *
 * This class scans provided controller objects for annotated handler methods
 * (via [Router]) and exposes them under a simple HTTP interface.
 *
 * Example usage:
 * ```
 * class HelloController {
 *     @Mapping("/hello")
 *     fun sayHello() = jsonObject { "message" to "Hello, world!" }
 * }
 *
 * fun main() {
 *     GetJson(HelloController()).start(8080)
 * }
 * ```
 *
 * @param controllers A vararg list of controller instances to be routed.
 */
class GetJson(vararg controllers: Any) {
    private val router = Router(controllers.toList())

    /**
     * Starts the HTTP server on the specified [port].
     *
     * The server listens on the root path ("/") and delegates incoming requests
     * to the [Router] for matching and handling. Responses are returned with
     * `Content-Type: application/json`.
     *
     * @param port The TCP port to bind the server to.
     */
    fun start(port: Int) {
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/") { exchange ->
            try {
                val response = router.handleRequest(
                    exchange.requestURI,
                    exchange.requestMethod
                )

                exchange.responseHeaders.add("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
                exchange.responseBody.use { os ->
                    os.write(response.toByteArray())
                }
            } catch (e: NotFoundException) {
                exchange.sendResponseHeaders(404, -1)
            } catch (e: Exception) {
                exchange.sendResponseHeaders(500, -1)
                exchange.responseBody.use { os ->
                    os.write("""{"error":"${e.message}"}""".toByteArray())
                }
            }
        }
        server.executor = null // Uses the default executor
        server.start()
        println("Server running on port $port")
    }
}

/**
 * Exception used to indicate that a route was not found.
 *
 * This results in a 404 HTTP response when thrown during request handling.
 *
 * @param message A message describing the missing route.
 */
class NotFoundException(message: String) : Exception(message)
