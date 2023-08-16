package com.efuntikov.adapter

import com.efuntikov.logger
import com.efuntikov.model.okx.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val httpClient = HttpClient(OkHttp) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

class OkxAdapter {
    private val properties: Properties = Properties()

    init {
        try {
            properties.load(FileInputStream("local.properties"))
        } catch (ex: IOException) {
            logger().error("Failed to load local properties", ex)
        }
    }

    private fun buildRequestHash(httpMethod: HttpMethod, requestPath: String, timestamp: String): String? {
        val apiKey = properties.getProperty("okx.api_key")
        val secretKey = properties.getProperty("okx.secret_key")
        logger().debug("OKX secret key: $secretKey")

        val toHash = (timestamp + httpMethod.value + requestPath).toByteArray()

        val hmacSha256 = try {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
            mac.init(secretKeySpec)
            mac.doFinal(toHash)
        } catch (e: Exception) {
            logger().error("Failed to calculate hmac-sha256", e)
            return null
        }

        return Base64.getEncoder().encode(hmacSha256).toString(Charsets.UTF_8).also {
            logger().debug("Resulting hash in base64: $it")
        }
    }

    private suspend fun subscribeOnRates() {
//        wss://ws.okx.com:8443/ws/v5/business
        httpClient.wss(method = HttpMethod.Get, host = "ws.okx.com", port = 8443, path = "/ws/v5/business", request = {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            setBody(
                GenericWebsocketRequest(
                    operation = "subscribe", args = listOf(
                        WsSpreadRequest(
                            channel = "sprd-tickers", spreadId = "BTC-USDT_BTC-USDT-SWAP"
                        )
                    )
                )
            )
        }) {
            // this: DefaultClientWebSocketSession
            sendSerialized(
                GenericWebsocketRequest(
                    operation = "subscribe", args = listOf(
                        WsSpreadRequest(
                            channel = "sprd-tickers", spreadId = "BTC-USDT_BTC-USDT-SWAP"
                        )
                    )
                )
            )


            val subscribeResponse = receiveDeserialized<WebsocketResponse<WsSpreadRequest>>()

            logger().debug("Subscribed: $subscribeResponse")

            while (true) {
                val tickersData = receiveDeserialized<WsDataResponse<WsSpreadRequest, WsTickersData>>()
                logger().debug("Tickers: $tickersData")
            }
        }
    }

    private suspend fun buildAuthHeaders(
        httpMethod: HttpMethod,
        requestPath: String,
        builder: HttpRequestBuilder
    ) = formatTimestamp(getServerTime())?.let { timestamp ->
        buildRequestHash(httpMethod, requestPath, timestamp)?.let { sign ->
            builder.headers {
//            append(HttpHeaders.ContentType, "application/json")
                append("OK-ACCESS-KEY", properties.getProperty("okx.api_key"))
                append("OK-ACCESS-SIGN", sign)
                append("OK-ACCESS-TIMESTAMP", timestamp)
                append("OK-ACCESS-PASSPHRASE", properties.getProperty("okx.passphrase"))
            }
        }
    }

    private suspend fun getServerTime(): Long {
        val response: GenericResponse<Timestamp> = httpClient.get("https://www.okx.com/api/v5/public/time").body()

        logger().debug("Timestamp response: $response")

        return response.data?.let {
            return@let if (it.isEmpty()) 0L else it[0].timestamp
        } ?: 0L
    }

    private fun formatTimestamp(timestamp: Long) = try {
        // example: 2020-12-08T09:08:57.715Z
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val netDate = Date(timestamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        logger().error("Failed to format server timestamp: $timestamp")
        null
    }

    suspend fun testRequest() {
//        val response: GenericResponse<Void> = httpClient.get("https://www.okx.com/api/v5/account/balance") {
//            buildAuthHeaders(HttpMethod.Get, "/api/v5/account/balance", this)
//        }.body()
//        logger().debug("Test response: $response")

        subscribeOnRates()
    }
}
