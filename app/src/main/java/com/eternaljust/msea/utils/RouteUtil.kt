package com.eternaljust.msea.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat.getSystemService

object RouteName {
    const val HOME = "home"
    const val NOTICE = "notice"
    const val NODE = "node"
    const val TOPIC_DETAIL = "topic_detail"
    const val PROFILE_TOPIC = "profile_topic"
    const val PROFILE_FRIEND = "profile_friend"
    const val PROFILE_FAVORITE = "profile_favorite"
    const val PROFILE_CREDIT = "profile_credit"
    const val PROFILE_GROUP= "profile_group"
    const val SETTING = "setting"
    const val ABOUT = "about"
    const val ABOUT_LICENSE = "about_license"
    const val ABOUT_SOURCE_CODE = "about_source_code"
    const val ABOUT_SDK_LIST = "about_sdk_list"
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