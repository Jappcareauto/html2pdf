package com.elroykanye.payload

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.io.IOException
import java.io.InputStream
import java.io.Serializable

data class HtmlFileResponsePayload(
    val html: String?,
    val name: String?,
    val extension: String?
) : Serializable {
    constructor(content: String?) : this(content, "file", null)

    fun getResponseEntity(inputStream: InputStream, extension: String?): ResponseEntity<ByteArrayResource> {
        try {
            return getResponseEntity(inputStream.readAllBytes())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun getResponseEntity(data: ByteArray?): ResponseEntity<ByteArrayResource> {
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(ByteArrayResource(data!!))
    }

    private val headers: HttpHeaders
        get() {
            val filename = this.name ?: "file"
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.contentDisposition = ContentDisposition.attachment()
                .filename(String.format("%s%s", filename, extension))
                .name(filename)
                .build()
            headers.accessControlExposeHeaders = listOf("Content-Disposition")
            return headers
        }
}
