package com.eternaljust.msea.ui.widget

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun mseaSmallTopAppBarColors(): TopAppBarColors {
    return TopAppBarDefaults.smallTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = Color.White,
        actionIconContentColor = Color.White,
        navigationIconContentColor = Color.White
    )
}

@Composable
fun mseaCenterAlignedTopAppBarColors(): TopAppBarColors {
    return TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = Color.White,
        actionIconContentColor = Color.White,
        navigationIconContentColor = Color.White
    )
}