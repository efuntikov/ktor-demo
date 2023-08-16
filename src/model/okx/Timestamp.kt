package com.efuntikov.model.okx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Timestamp(
    @SerialName("ts")
    val timestamp: Long
)
