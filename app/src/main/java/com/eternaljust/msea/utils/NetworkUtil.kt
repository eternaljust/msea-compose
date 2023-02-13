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

        fun getData(url: String): String {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            val response = httpClient
                .newCall(request)
                .execute()
            response.body?.string()?.let {
                return it
            }
            return ""
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
                return uids.last().replace(".html", "")
            } else if (text.contains("mod=space&uid=")) {
                return text.split("mod=space&uid=").last()
            }
            return ""
        }

        fun getTid(text: String): String {
            if (text.contains("thread-") && text.contains(".html")) {
                val uids = text.split("thread-")
                return uids.last().split("-").first()
            } else if (text.contains("mod=viewthread&tid=")) {
                return text.split("mod=viewthread&tid=").last()
            } else if (text.contains("&ptid=")) {
                return text.split("&ptid=").last()
                    .split("&pid=").first()
            }
            return ""
        }

        fun getFid(text: String): String {
            if (text.contains("forum-") && text.contains("-1.html")) {
                val ids = text.split("forum-")
                return ids.last().replace("-1.html", "")
            } else if (text.contains("fid=")) {
                return text.split("fid=")[1]
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
                    val formhash1 = hash.split("=").last()
                    println("formhash=${formhash1}")
                    UserInfo.instance.formhash = formhash1
                }
            }
        }
    }
}

object HTMLURL {
    const val APP_GITHUB = "https://github.com/eternaljust/msea-compose"
    const val APP_RELEASE = "$APP_GITHUB/releases"
    const val GET_VERSION = "https://gitee.com/eternaljust/app-config/raw/main/msea-compose/version.json"

    const val BASE = "https://www.chongbuluo.com"

    const val LOGIN = "$BASE/member.php?mod=logging&action=login&loginsubmit=yes"
    const val PROFILE = "$BASE/home.php?mod=space&do=profile&from=space&uid="
    const val GET_DAY_SIGN = "$BASE/plugin.php?id=wq_sign"
    const val SIGN_MESSAGE = GET_DAY_SIGN + "&mod=mood&infloat=yes&confirmsubmit=yes" +
            "&handlekey=pc_click_wqsign"
    const val SIGN_LIST = "$BASE/plugin.php?id=wq_sign&mod=info"
    const val TOPIC_LIST = "$BASE/forum.php?mod=guide"
    const val MY_POST_LIST = "$BASE/home.php?mod=space&do=notice&view=mypost"
    const val SYSTEM_LIST = "$BASE/home.php?mod=space&do=notice&view=system"
    const val INTERACTIVE_LIST = "$BASE/home.php?mod=space&do=notice&view=interactive&type=friend"
    const val NODE_LIST = "$BASE/forum.php?mod=index&gid="
    const val TAG_LIST = "$BASE/misc.php?mod=tag"
    const val PROFILE_TOPIC_LIST = "$BASE/home.php?mod=space&do=thread&view=me&order=dateline&from=space"
    const val PROFILE_FAVORITE_LIST = "$BASE/home.php?mod=space&do=favorite&type=thread"
    const val CREDIT_LIST = "$BASE/home.php?mod=spacecp&ac=credit"
    const val FRIEND_LIST = "$BASE/home.php?mod=space&do=friend"
    const val USER_GROUP_LIST = "$BASE/home.php?mod=spacecp&ac=usergroup"
    const val TOPIC_DETAIL = "$BASE/thread"
    const val FRIEND_DETAIL_LIST = "$BASE/home.php?mod=space&do=friend&from=space"
    const val SEARCH_USER = "$BASE/home.php?mod=spacecp&ac=search&searchsubmit=yes"
    const val SEARCH_POST = "$BASE/search.php?mod=forum&searchsubmit=yes&orderby=lastpost&ascdesc=desc"
}

sealed class HttpResult<out T> {
    data class Success<T>(val result: T): HttpResult<T>()
    data class Error(val exception: Exception): HttpResult<Nothing>()
}