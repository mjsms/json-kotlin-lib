package main.kotlin.api.server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

class RequestHandler(private val router: Router) : HttpHandler {
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