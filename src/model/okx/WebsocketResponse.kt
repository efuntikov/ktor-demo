package com.efuntikov.model.okx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebsocketResponse<T>(
    @SerialName("event")
    val operation: String,
    @SerialName("arg")
    val arg: T? = null,
    @SerialName("code")
    val errorCode: String? = null,
    @SerialName("msg")
    val message: String? = null
)
