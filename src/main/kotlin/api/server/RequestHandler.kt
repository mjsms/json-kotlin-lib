package main.kotlin.api.server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

/**
 * HTTP request handler that delegates URI and method matching to a [Router].
 *
 * This class implements [HttpHandler] and is designed to be used with a
 * Java SE [com.sun.net.httpserver.HttpServer] to handle incoming HTTP requests.
 *
 * It delegates the request to the provided [router], obtains the response as
 * a JSON string, and sends it back to the client with HTTP 200 OK.
 * In case of an error, it returns a 500 Internal Server Error.
 *
 * @param router The [Router] responsible for matching and executing the request.
 */
class RequestHandler(private val router: Router) : HttpHandler {

    /**
     * Handles an incoming HTTP request.
     *
     * This method delegates the request URI and HTTP method to the [Router],
     * and writes the JSON response to the response body.
     *
     * @param exchange The [HttpExchange] representing the HTTP request and response.
     */
    override fun handle(exchange: HttpExchange) {
        try {
            val response = router.handleRequest(exchange.requestURI, exchange.requestMethod)
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { os ->
                os.write(response.toByteArray())
            }
        } catch (e: Exception) {
            exchange.sendResponseHeaders(500, 0)
        }
    }
}
