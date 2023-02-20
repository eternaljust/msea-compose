package com.eternaljust.msea.utils

import android.content.Context
import com.umeng.analytics.MobclickAgent
import com.umeng.pagesdk.PageManger.getApplicationContext

// 友盟埋点统计自定义事件
class StatisticsTool {
    companion object {
        val instance by lazy { StatisticsTool() }
    }

    fun eventObject(
        event: String,
        keyAndValue: Map<String, Any>
    ) {
        MobclickAgent.onEventObject(getApplicationContext(), event, keyAndValue)
    }

    fun eventObject(
        context: Context,
        resId: Int,
        keyAndValue: Map<Int, Any>
    ) {
        val event = context.getString(resId)
        val params: MutableMap<String, Any> = mutableMapOf()
        keyAndValue.forEach { (k, v) ->
            params[context.getString(k)] = v
        }
        println("eventObject---event=${event}, params=$params")

        getApplicationContext()

        MobclickAgent.onEventObject(context, event, params)
    }
}