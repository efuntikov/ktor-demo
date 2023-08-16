package com.efuntikov.model.okx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenericWebsocketRequest<T>(
    @SerialName("op")
    val operation: String,
    @SerialName("args")
    val args: List<T>? = null
)
