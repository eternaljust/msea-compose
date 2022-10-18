package com.eternaljust.msea

import android.app.Application
import android.content.Context
import com.eternaljust.msea.utils.DataStoreUtil

class MseaApp: Application() {
    companion object {
        lateinit var CONTEXT: Context
    }

    override fun onCreate() {
        super.onCreate()
        CONTEXT = this
        DataStoreUtil.init(this)
    }
}