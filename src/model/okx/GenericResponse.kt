package com.efuntikov.model.okx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenericResponse<T>(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: List<T>? = null,
    @SerialName("msg")
    val message: String
)
