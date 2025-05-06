package main.kotlin.api.server

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class GetJson(vararg controllers: Any) {
    private val router = Router(controllers.toList())

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
        server.executor = null // Usa o executor padrão
        server.start()
        println("Server running on port $port")
    }
}

class NotFoundException(message: String) : Exception(message)