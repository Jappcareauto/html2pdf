package com.elroykanye.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.function.*
import java.util.zip.*

object StreamUtil {
    @Throws(IOException::class)
    fun compress(inputStream: InputStream, name: String, extension: String): ByteArrayInputStream {
        val byteOutStream = ByteArrayOutputStream()
        ZipOutputStream(byteOutStream).use { zipOutStream ->
            listOf(inputStream).forEach(
                Consumer {
                    try {
                        val zipEntry = ZipEntry(name + extension)
                        zipOutStream.putNextEntry(zipEntry)

                        zipOutStream.write(inputStream.readAllBytes())

                        zipOutStream.closeEntry()
                    } catch (ignored: Exception) {
                    }
                })
        }
        return ByteArrayInputStream(byteOutStream.toByteArray())
    }
}
