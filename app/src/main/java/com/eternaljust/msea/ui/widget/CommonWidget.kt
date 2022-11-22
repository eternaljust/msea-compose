package com.eternaljust.msea.ui.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ListArrowForward() {
    Icon(
        modifier = Modifier.size(20.dp),
        imageVector = Icons.Default.ArrowForwardIos,
        tint = Color.LightGray,
        contentDescription = null
    )
}