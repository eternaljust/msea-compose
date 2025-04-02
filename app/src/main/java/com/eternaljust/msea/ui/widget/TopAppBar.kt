package com.eternaljust.msea.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mseaTopAppBarColors(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = Color.White,
        actionIconContentColor = Color.White,
        navigationIconContentColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalTopAppBar(
    title: String = "",
    onClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
        },
        colors = mseaTopAppBarColors()
    )
}