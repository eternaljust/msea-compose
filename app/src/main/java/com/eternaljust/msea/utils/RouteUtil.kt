package com.eternaljust.msea.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.eternaljust.msea.BuildConfig

object RouteName {
    const val HOME = "home"
    const val NOTICE = "notice"
    const val NODE = "node"
    const val NODE_DETAIL = "node_detail"
    const val TOPIC_DETAIL = "topic_detail"
    const val PROFILE_TOPIC = "profile_topic"
    const val PROFILE_FRIEND = "profile_friend"
    const val PROFILE_FAVORITE = "profile_favorite"
    const val PROFILE_CREDIT = "profile_credit"
    const val PROFILE_GROUP= "profile_group"
    const val PROFILE_DETAIL = "profile_detail"
    const val PROFILE_DETAIL_USERNAME = "profile_detail_username"
    const val SETTING = "setting"
    const val ABOUT = "about"
    const val LOGIN = "login"
    const val LOGOUT= "logout"
    const val SIGN = "sign"
    const val TAG = "tag"
    const val TAG_LIST = "tag_list"
    const val NODE_LIST = "node_list"
    const val TERMS_OF_SERVICE = "terms_of_service"
    const val WEBVIEW = "webview"
    const val LICENSE = "license"
    const val SOURCE_CODE = "source_code"
    const val SDK_LIST = "sdk_list"
}

fun isAppInstalled(
    packageName: String,
    context: Context
) : Boolean {
    val pm = context.packageManager
    // 系统应用uid从1000开始，用户应用uid从10000(FIRST_APPLICATION_UID)开始，直接合并查询
    for (i in 10000..11000) {
        try {
            val apps = pm.getPackagesForUid(i)
            if (apps != null) {
                for (app in apps) {
                    val info = pm.getPackageInfo(app!!, 0)
                    if (info != null && info.packageName == packageName) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

fun openSystemBrowser(
    url: String,
    context: Context
) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun openApp(
    url: String,
    context: Context
) {
    val intent: Intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(url)
    }
    context.startActivity(intent)
}

fun openSystemShare(
    text: String,
    title: String = "",
    context: Context
) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_TITLE, title)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun textCopyThenPost(
    textCopied: String,
    context: Context
) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    // When setting the clip board text.
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", textCopied))
    // Only show a toast for Android 12 and lower.
}

fun sendEmail(context: Context) {
    val toRecipient = Uri.parse("mailto:eternal.just@gmail.com")
    val title = "Msea ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) 问题反馈"
    val content = """
设备来自：${Build.BRAND} ${Build.MODEL} / Android ${Build.VERSION.RELEASE}

1.描述遇到的问题，方便的话添加错误页面截图。


2.能否复现问题？可以的话给出具体的步骤。


3.非 bug 反馈，有其他的想法。

"""

    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = toRecipient
    intent.putExtra(Intent.EXTRA_SUBJECT, title)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    context.startActivity(intent)
}