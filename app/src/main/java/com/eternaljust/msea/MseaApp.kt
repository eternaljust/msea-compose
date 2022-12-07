package com.eternaljust.msea

import android.app.Application
import android.content.Context
import com.eternaljust.msea.utils.DataStoreUtil
import com.umeng.commonsdk.UMConfigure

class MseaApp: Application() {
    companion object {
        lateinit var CONTEXT: Context
    }

    override fun onCreate() {
        super.onCreate()

        CONTEXT = this
        DataStoreUtil.init(this)

        val appkey = "637ddbe888ccdf4b7e6cbd1e"
        val channel = "GitHub"
        // 友盟预初始化
        UMConfigure.preInit(this, appkey, channel)
        // 友盟正式初始化
        UMConfigure.init(this, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, "")
    }
}