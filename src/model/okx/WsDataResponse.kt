package com.efuntikov.model.okx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsDataResponse<Arg, Data>(
    @SerialName("arg")
    val arg: Arg? = null,
    @SerialName("data")
    val data: List<Data>? = null
)
