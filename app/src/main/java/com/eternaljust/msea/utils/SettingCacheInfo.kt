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
}

object SettingInfoKey {
    const val COLOR_SCHEME = "colorSchemeKey"
    const val THEME_STYLE = "themeStyleKey"
}