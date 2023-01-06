package com.eternaljust.msea

import android.app.Application
import android.content.Context
import com.eternaljust.msea.utils.Constants
import com.eternaljust.msea.utils.DataStoreUtil
import com.eternaljust.msea.utils.SettingInfo
import com.umeng.commonsdk.UMConfigure

class MseaApp: Application() {
    companion object {
        lateinit var CONTEXT: Context
    }

    override fun onCreate() {
        super.onCreate()

        CONTEXT = this
        DataStoreUtil.init(this)

        val appkey = Constants.umAppkey
        val channel = "GitHub"
        // 友盟预初始化
        UMConfigure.preInit(this, appkey, channel)
        if (SettingInfo.instance.agreePrivacyPolicy) {
            // 友盟正式初始化
            UMConfigure.init(this, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, "")
        }
    }
}