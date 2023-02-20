package com.eternaljust.msea

import android.app.Application
import android.content.Context
import com.eternaljust.msea.utils.Constants
import com.eternaljust.msea.utils.DataStoreUtil
import com.eternaljust.msea.utils.SettingInfo
import com.eternaljust.msea.utils.StatisticsTool
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
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true)
        if (SettingInfo.instance.agreePrivacyPolicy) {
            // 友盟正式初始化
            UMConfigure.init(this, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, "")
            StatisticsTool.instance.eventObject(
                context = this,
                resId = R.string.event_page_home,
                keyAndValue = mapOf(
                    R.string.key_tab to "虫部落"
                )
            )
        }
    }
}