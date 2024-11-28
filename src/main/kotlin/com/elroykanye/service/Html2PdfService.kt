package com.elroykanye.service


import com.microsoft.playwright.Browser
import com.microsoft.playwright.Browser.NewContextOptions
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.Playwright.CreateOptions
import com.microsoft.playwright.PlaywrightException
import com.microsoft.playwright.options.WaitUntilState
import com.elroykanye.payload.HtmlFileRequestPayload
import com.elroykanye.payload.HtmlFileResponsePayload
import com.elroykanye.util.StreamUtil
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class Html2PdfService {
    private val log = getLogger(javaClass)
    private var browser: Browser? = null
    private var playwrightBrowserContext: BrowserContext? = null

    private var initialised = false

    @Throws(IOException::class)
    @PostConstruct
    fun init() {
        if (!initialised) initPlaywright()
    }

    private fun initPlaywright() {
        try {
            val createOptions = CreateOptions()
            val playwright = Playwright.create(createOptions)
            val launchOptions = LaunchOptions()
            launchOptions.setHeadless(true)
            browser = playwright.chromium().launch(launchOptions)
            playwrightBrowserContext = browser!!.newContext(NewContextOptions())
            initialised = true
            log.info("Launched playwright using chromium: {}", browser!!.version())
        } catch (e: Exception) {
            initialised = false
        }
    }

    fun convert(html: String?): InputStream {
        return convert(html, false)
    }

    fun convert(html: String?, compress: Boolean): InputStream {
        return convert(HtmlFileResponsePayload(html), compress)
    }

    fun convert(payload: HtmlFileResponsePayload, compress: Boolean): InputStream {
        val name: String = Optional.ofNullable(payload.name).orElse(UUID.randomUUID().toString())
        val extension: String = Optional.ofNullable(payload.extension).orElse("")

        try {
            Files.createDirectories(Paths.get("app/dump/docs").toAbsolutePath())
            val htmlFilePath = "app/dump/docs/" + UUID.randomUUID() + ".html"
            val pdfFilePath = "app/dump/docs/" + UUID.randomUUID() + ".pdf"
            val htmlAbsFilePath = Paths.get(htmlFilePath).toAbsolutePath()
            val pdfAbsFilePath = Paths.get(pdfFilePath).toAbsolutePath()

            if (!Files.exists(htmlAbsFilePath)) Files.createFile(htmlAbsFilePath)

            val writer = OutputStreamWriter(FileOutputStream(htmlFilePath))
            writer.write(payload.html!!)
            writer.close()

            val htmlFileUri = htmlAbsFilePath.toUri()
            val htmlFileUrl = "file://" + htmlFileUri.path
            log.info("Converting file started: {}", htmlFileUrl)

            val convertedInputStream = convertPlaywright(htmlFileUrl, pdfAbsFilePath)

            Files.deleteIfExists(pdfAbsFilePath)
            Files.deleteIfExists(htmlAbsFilePath)
            log.info("Converting file complete: {}", htmlFileUrl)

            return if (!compress) convertedInputStream
            else {
                StreamUtil.compress(convertedInputStream, name, extension)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun convertPlaywright(htmlFileUrl: String, pdfAbsFilePath: Path): InputStream {
        try {
            initPlaywright()

            val browserContext = playwrightBrowserContext ?: run {
                Playwright.create(CreateOptions())
                    .chromium()
                    .launch(LaunchOptions())
                    .newContext(NewContextOptions())
            }

            val page = browserContext.newPage()

            val navigateOptions: Page.NavigateOptions = Page.NavigateOptions()
            navigateOptions.setWaitUntil(WaitUntilState.NETWORKIDLE)
            page.navigate(htmlFileUrl, navigateOptions)

            val pdfOptions: Page.PdfOptions = getPagePdfOptions(pdfAbsFilePath)
            val stream = ByteArrayInputStream(page.pdf(pdfOptions))

            page.close()

            return stream
        } catch (e: PlaywrightException) {
            initialised = false
            throw RuntimeException(e)
        }
    }

    private fun getPagePdfOptions(output: Path): Page.PdfOptions {
        val pagePdfOptions: Page.PdfOptions = Page.PdfOptions()
        pagePdfOptions.displayHeaderFooter = true
        pagePdfOptions.printBackground = true
        pagePdfOptions.landscape = false
        pagePdfOptions.path = output
        return pagePdfOptions
    }

    fun destroy() {
        if (browser != null) {
            browser!!.close()
            log.info("Closed puppeteer browser object")
        }
    }
}
