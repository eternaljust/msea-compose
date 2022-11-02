package com.eternaljust.msea.utils

import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception
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
                            if (it.name.contains("saltkey")) {
                                val salt = "${it.name}=${it.value}"
                                println("cookie salt = $salt")
                                UserInfo.instance.salt = salt
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

            val document = Jsoup.connect(url)
                .userAgent(userAgent)
                .header("Content-Type", contentType)
                .header("Cookie", getCookies())
                .get()
            println(document.html())
            getFormhash(document = document)

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
            if (UserInfo.instance.formhash.isNotEmpty()) {
                builder.add("formhash", UserInfo.instance.formhash)
            }
            if (params.isNotEmpty()) { params.forEach { (t, u) -> builder.add(t, u) } }
            if (encodedParams.isNotEmpty()) {
                encodedParams.forEach { (t, u) ->
                    builder.addEncoded(t, u)
                }
            }

            val requestBody = builder.build()
            val reqBuilder = Request.Builder()
            val request = reqBuilder.url(url)
                .post(requestBody)
                .header("Cookie", getCookies())
                .build()
            val response = httpClient.newCall(request).execute()
            val html = response.body?.string()

            println("response html begin!")
            println(html)
            println("response html end!")

            var document = Jsoup.parse("")
            html?.let {
               document = Jsoup.parse(it)
            }
            getFormhash(document = document)

            return document
        }

        fun urlEncode(param: String): String {
            return URLEncoder.encode(param, "UTF-8")
        }

        fun getAvatar(url: String): String {
            var avatar = url.replace("&size=small", "")
            avatar = avatar.replace("&size=middle", "")
            avatar = avatar.replace("&size=big", "")
            if (!avatar.contains(HTMLURL.BASE)) {
                avatar = HTMLURL.BASE + "/" + avatar
            }
            return avatar
        }

        fun getUid(text: String): String {
            if (text.contains("uid-") && text.contains(".html")) {
                val uids = text.split("uid-")
                val uid = uids.last().replace(".html", "")
                return uid
            }
            return ""
        }

        private fun getCookies(): String {
            val cookie = UserInfo.instance.salt + "; " + UserInfo.instance.auth
            println("Cookie = $cookie")
            return cookie
        }

        private fun getFormhash(document: Document) {
            val myinfo = document.selectXpath("//div[@id='myinfo']//a[4]").attr("href")
            if (myinfo.isNotEmpty() && myinfo.contains("formhash") &&
                myinfo.contains("&")) {
                val components = myinfo.split("&")
                val formhash = components.last()
                val last = formhash.split("=").last()
                if (last.isNotEmpty()) {
                    println("formhash=${last}")
                    UserInfo.instance.formhash = last
                }
            }

            val formhash = document.selectXpath("//input[@id='formhash']")
                .attr("value")
            if (formhash.isNotEmpty()) {
                println("formhash=${formhash}")
                UserInfo.instance.formhash = formhash
            }

            val href = document.selectXpath("//div[@id='toptb']//a[6]")
                .attr("href")
            if (href.isNotEmpty() && href.contains("formhash")) {
                val hash = href.split("&").last()
                if (hash.contains("=")) {
                    val formhash = hash.split("=").last()
                    println("formhash=${formhash}")
                    UserInfo.instance.formhash = formhash
                }
            }
        }
    }
}

object HTMLURL {
    const val BASE = "https://www.chongbuluo.com"

    const val LOGIN = "$BASE/member.php?mod=logging&action=login&loginsubmit=yes"
    const val PROFILE = "$BASE/home.php?mod=space&uid="
    const val GET_DAY_SIGN = "$BASE/plugin.php?id=wq_sign"
    const val SIGN_MESSAGE = GET_DAY_SIGN + "&mod=mood&infloat=yes&confirmsubmit=yes" +
            "&handlekey=pc_click_wqsign"
    const val SIGN_LIST = "$BASE/plugin.php?id=wq_sign&mod=info"
    const val TOPIC_LIST = "$BASE/forum.php?mod=guide"
    const val MY_POST_LIST = "$BASE/home.php?mod=space&do=notice&view=mypost"
    const val SYSTEM_LIST = "$BASE/home.php?mod=space&do=notice&view=system"
    const val INTERACTIVE_LIST = "$BASE/home.php?mod=space&do=notice&view=interactive&type=friend"
}

sealed class HttpResult<out T> {
    data class Success<T>(val result: T): HttpResult<T>()
    data class Error(val exception: Exception): HttpResult<Nothing>()
}