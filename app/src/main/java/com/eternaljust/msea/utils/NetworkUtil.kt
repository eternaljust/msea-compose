package com.eternaljust.msea.utils

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class NetworkUtil private constructor() {
    companion object {
        private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
        private val contentType = "application/text/html; charset=utf-8"

        val httpClient by lazy {
            val builder = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .build()
                    chain.proceed(request)
                }
            builder.build()
        }

        fun getRequest(
            url: String
        ): Document {
            println("\ngetRequest.url=$url")

            val document = Jsoup.connect(url)
                .userAgent(userAgent)
                .header("Content-Type", contentType)
                .get()
            println(document.html())

            return document
        }

        fun postRequest(
            url: String,
            params: Map<String, String>,
            encodedParams: Map<String, String> = emptyMap()
        ): Document {
            println("\npostRequest.url=$url")
            println("params=$params")
            println("encodedParams=$encodedParams")

            val builder = FormBody.Builder()
            params.forEach { t, u ->
                builder.add(t, u)
            }
            if (encodedParams.isNotEmpty()) {
                encodedParams.forEach { t, u ->
                    builder.addEncoded(t, u)
                }
            }

            val requestBody = builder.build()
            val reqBuilder = Request.Builder()
            val request = reqBuilder.url(url)
                .post(requestBody)
                .build()
            val response = httpClient.newCall(request).execute()
            val html = response.body?.string()
            println("response html begin!")
            println(html)
            println("response html end!")

            val document = Jsoup.parse(html)

            return document
        }
    }
}
