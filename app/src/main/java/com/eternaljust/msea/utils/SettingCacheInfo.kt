package com.eternaljust.msea.utils

class SettingInfo {
    companion object {
        val instance by lazy { SettingInfo() }
    }

    var colorScheme: Boolean
        get() = DataStoreUtil.getData(SettingInfoKey.COLOR_SCHEME, false)
        set(value) = DataStoreUtil.syncSetData(SettingInfoKey.COLOR_SCHEME, value)

    var themeStyle: Int
        get() = DataStoreUtil.getData(SettingInfoKey.THEME_STYLE, 0)
        set(value) = DataStoreUtil.syncSetData(SettingInfoKey.THEME_STYLE, value)

    var daysignSwitch: Boolean
        get() = DataStoreUtil.getData(SettingInfoKey.DAY_SIGN_SWITCH, false)
        set(value) = DataStoreUtil.syncSetData(SettingInfoKey.DAY_SIGN_SWITCH, value)

    var daysignHour: Int
        get() = DataStoreUtil.getData(SettingInfoKey.DAY_SIGN_HOUR, 8)
        set(value) = DataStoreUtil.syncSetData(SettingInfoKey.DAY_SIGN_HOUR, value)

    var daysignMinute: Int
        get() = DataStoreUtil.getData(SettingInfoKey.DAY_SIGN_MINUTE, 0)
        set(value) = DataStoreUtil.syncSetData(SettingInfoKey.DAY_SIGN_MINUTE, value)
}

object SettingInfoKey {
    const val COLOR_SCHEME = "colorSchemeKey"
    const val THEME_STYLE = "themeStyleKey"
    const val DAY_SIGN_SWITCH = "daysignSwitchKey"
    const val DAY_SIGN_HOUR = "daysignHourKey"
    const val DAY_SIGN_MINUTE = "daysignMinuteKey"
}