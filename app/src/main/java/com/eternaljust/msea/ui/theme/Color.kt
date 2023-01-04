package com.eternaljust.msea.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.eternaljust.msea.utils.SettingInfo

val md_theme_light_primary = Color(0xFF006E26)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFF7AFD8A)
val md_theme_light_onPrimaryContainer = Color(0xFF002106)
val md_theme_light_secondary = Color(0xFF4355B9)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFDEE0FF)
val md_theme_light_onSecondaryContainer = Color(0xFF00105C)
val md_theme_light_tertiary = Color(0xFF855300)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFDDB8)
val md_theme_light_onTertiaryContainer = Color(0xFF2A1700)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFCFDF7)
val md_theme_light_onBackground = Color(0xFF1A1C19)
val md_theme_light_surface = Color(0xFFFCFDF7)
val md_theme_light_onSurface = Color(0xFF1A1C19)
val md_theme_light_surfaceVariant = Color(0xFFDEE5D9)
val md_theme_light_onSurfaceVariant = Color(0xFF424940)
val md_theme_light_outline = Color(0xFF72796F)
val md_theme_light_inverseOnSurface = Color(0xFFF0F1EB)
val md_theme_light_inverseSurface = Color(0xFF2F312D)
val md_theme_light_inversePrimary = Color(0xFF5CE071)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF006E26)
val md_theme_light_surfaceTintColor = Color(0xFF006E26)

val md_theme_dark_primary = Color(0xFF5CE071)
val md_theme_dark_onPrimary = Color(0xFF003910)
val md_theme_dark_primaryContainer = Color(0xFF00531B)
val md_theme_dark_onPrimaryContainer = Color(0xFF7AFD8A)
val md_theme_dark_secondary = Color(0xFFBAC3FF)
val md_theme_dark_onSecondary = Color(0xFF08218A)
val md_theme_dark_secondaryContainer = Color(0xFF293CA0)
val md_theme_dark_onSecondaryContainer = Color(0xFFDEE0FF)
val md_theme_dark_tertiary = Color(0xFFFFB960)
val md_theme_dark_onTertiary = Color(0xFF472A00)
val md_theme_dark_tertiaryContainer = Color(0xFF653E00)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDDB8)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF1A1C19)
val md_theme_dark_onBackground = Color(0xFFE2E3DD)
val md_theme_dark_surface = Color(0xFF1A1C19)
val md_theme_dark_onSurface = Color(0xFFE2E3DD)
val md_theme_dark_surfaceVariant = Color(0xFF424940)
val md_theme_dark_onSurfaceVariant = Color(0xFFC2C9BD)
val md_theme_dark_outline = Color(0xFF8C9388)
val md_theme_dark_inverseOnSurface = Color(0xFF1A1C19)
val md_theme_dark_inverseSurface = Color(0xFFE2E3DD)
val md_theme_dark_inversePrimary = Color(0xFF006E26)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFF5CE071)
val md_theme_dark_surfaceTintColor = Color(0xFF5CE071)


val seed = Color(0xFF53D769)

@Composable
fun colorTheme(
    light: Color,
    dark: Color
): Color {
    val themeStyle = SettingInfo.instance.themeStyle
    return if (themeStyle == 0) {
        if (isSystemInDarkTheme()) dark else light
    } else if (themeStyle == 1) {
        light
    } else {
        dark
    }
}

@Composable
fun getIconTintColorSecondary(isNodeFid125: Boolean): Color {
    return if (isNodeFid125) Color.Gray else MaterialTheme.colorScheme.secondary
}

@Composable
fun getIconTintColorPrimary(isNodeFid125: Boolean): Color {
    return if (isNodeFid125) Color.Gray else MaterialTheme.colorScheme.primary
}