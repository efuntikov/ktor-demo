package com.efuntikov.model.okx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsSpreadRequest(
    @SerialName("channel")
    val channel: String,
    @SerialName("sprdId")
    val spreadId: String
)
