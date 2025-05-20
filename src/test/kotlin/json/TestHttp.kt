package main.kotlin.api.server

import kotlin.test.assertEquals
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.BeforeClass
import org.junit.Test
import json.startServer

class GenericControllerIntegrationTest {
    companion object {
        private val client = OkHttpClient()

        @BeforeClass
        @JvmStatic
        fun setup() {
            Thread { startServer() }.start()
            Thread.sleep(500)
        }
    }

    private fun call(path: String): String {
        val req = Request.Builder()
            .url("http://localhost:8080/$path")
            .get()
            .build()

        client.newCall(req).execute().use { resp ->
            assertEquals(200, resp.code, "Esperava 200 em /$path")
            return resp.body!!.string()
        }
    }

    /** Remove *todos* espaços, quebras de linha e tabs para comparação exata */
    private fun compactJson(raw: String): String =
        raw.replace(Regex("\\s+"), "")

    @Test
    fun `GET api generic ints deve devolver 1,2,3`() {
        val body = compactJson(call("api/generic/ints"))
        assertEquals("[1,2,3]", body)
    }

    @Test
    fun `GET api generic pair deve devolver o objeto correto`() {
        val body = compactJson(call("api/generic/pair"))
        assertEquals("""{"first":"um","second":"dois"}""", body)
    }

    @Test
    fun `GET api generic path a deve devolver a exclamação`() {
        val body = compactJson(call("api/generic/path/a"))
        assertEquals("\"a!\"", body)
    }

    @Test
    fun `GET api generic path b deve devolver b exclamação`() {
        val body = compactJson(call("api/generic/path/b"))
        assertEquals("\"b!\"", body)
    }

    @Test
    fun `GET api generic args n equals 3 text PA deve devolver PAPAPA`() {
        val body = compactJson(call("api/generic/args?n=3&text=PA"))
        assertEquals("""{"PA":"PAPAPA"}""", body)
    }
}
