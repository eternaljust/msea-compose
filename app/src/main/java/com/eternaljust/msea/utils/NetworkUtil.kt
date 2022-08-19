package com.eternaljust.msea.utils

import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder

class NetworkUtil private constructor() {
    companion object {
        private const val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
        private const val contentType = "application/text/html; charset=utf-8"

        private val httpClient by lazy {
            val builder = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(false)
                .addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .build()
                    chain.proceed(request)
                }
                .cookieJar(object : CookieJar {
                    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                        cookies.forEach {
                            println("cookie=${it.name}, value=${it.value}")
                            if (it.name.contains("auth") && it.value != "deleted") {
                                val auth = "${it.name}=${it.value}"
                                println("cookie auth = $auth")
                                UserInfo.instance.auth = auth
                            }
                        }
                    }

                    override fun loadForRequest(url: HttpUrl): List<Cookie> {
                        val cookies: ArrayList<Cookie> = ArrayList()
                        val auth = UserInfo.instance.auth
                        println("auth=$auth")
                        println("HttpUrl=$url")
                        return cookies
                    }
                })

            builder.build()
        }

        fun getRequest(
            url: String
        ): Document {
            println("\ngetRequest.url=$url")
            val cookie = DataStoreUtil.getData(UserInfoKey.AUTH, "")
            println("cookie auth = $cookie")

            val document = Jsoup.connect(url)
                .userAgent(userAgent)
                .header("Content-Type", contentType)
                .header("Cookie", cookie)
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
            params.forEach { (t, u) -> builder.add(t, u) }
            if (encodedParams.isNotEmpty()) {
                encodedParams.forEach { (t, u) ->
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

            html?.let {
                return Jsoup.parse(it)
            }
            return Jsoup.parse("")
        }

        fun urlEncode(param: String): String {
            return URLEncoder.encode(param, "UTF-8")
        }
    }
}

object HTMLURL {
    const val BASE = "https://www.chongbuluo.com"

    const val LOGIN = BASE + "/member.php?mod=logging&action=login&loginsubmit=yes"
    const val PROFILE = BASE + "/home.php?mod=space&uid="
}