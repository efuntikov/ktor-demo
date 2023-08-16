package com.efuntikov

//import io.ktor.server.websocket.*
//import io.ktor.websocket.*
import com.efuntikov.adapter.OkxAdapter
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

//val httpClient = HttpClient(Java) {
//    install(Logging) {
//        logger = Logger.DEFAULT
//        level = LogLevel.HEADERS
//    }
//    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
//        json()
//    }
//}

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
//    install(WebSockets) {
//        pingPeriod = Duration.ofSeconds(15)
//        timeout = Duration.ofSeconds(15)
//        maxFrameSize = Long.MAX_VALUE
//        masking = false
//    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

//        webSocket("/myws/echo") {
//            send(Frame.Text("Hi from server"))
//            while (true) {
//                val frame = incoming.receive()
//                if (frame is Frame.Text) {
//                    send(Frame.Text("Client said: " + frame.readText()))
//                }
//            }
//        }

//        get("/json/gson") {
//            call.respond(mapOf("hello" to "world"))
//        }
    }

    test()
}

private fun test() {
    runBlocking {
        OkxAdapter().testRequest()
    }
}

