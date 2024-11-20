package com.elroykanye.controller


import com.elroykanye.payload.HtmlFileResponsePayload
import com.elroykanye.service.Html2PdfService
import org.springframework.core.io.InputStreamResource
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Html2PdfController(
    private val service: Html2PdfService
) {
    @PostMapping("/convert")
    fun convert(@RequestBody payload: HtmlFileResponsePayload): InputStreamResource {
        val pdfIs = service.convert(payload, false)
        return InputStreamResource(pdfIs)
    }
}
