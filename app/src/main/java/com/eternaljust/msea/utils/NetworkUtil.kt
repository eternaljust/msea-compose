package com.eternaljust.msea.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class NetworkUtil private constructor() {
    companion object {
        private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
        private val contentType = "application/text/html; charset=utf-8"

        fun getRequest(
            url: String
        ): Document {
            val document = Jsoup.connect(url)
                .userAgent(userAgent)
                .header("Content-Type", contentType)
                .get()
            println(document.html())

            return document
        }

        fun postRequest(
            url: String,
            params: Map<String, String>
        ): Document {
            val document = Jsoup.connect(url)
                .userAgent(userAgent)
                .header("Content-Type", contentType)
                .data(params)
                .followRedirects(true)
                .post()
            println(document.html())

            return document
        }
    }
}
