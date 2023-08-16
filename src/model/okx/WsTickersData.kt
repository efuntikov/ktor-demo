package com.efuntikov.model.okx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsTickersData(
    @SerialName("sprdId")
    val spreadId: String,
    @SerialName("last")
    val last: String,
    @SerialName("lastSz")
    val lastSz: String,
    @SerialName("askPx")
    val askPx: String,
    @SerialName("askSz")
    val askSz: String,
    @SerialName("bidPx")
    val bidPx: String,
    @SerialName("bidSz")
    val bidSz: String,
    @SerialName("ts")
    val ts: String
)
