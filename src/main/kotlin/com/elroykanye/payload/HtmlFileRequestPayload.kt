package com.elroykanye.payload

import java.io.Serializable

data class HtmlFileRequestPayload(
    val html: String,
    val name: String
) : Serializable
