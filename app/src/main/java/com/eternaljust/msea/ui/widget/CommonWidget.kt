package com.eternaljust.msea.ui.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eternaljust.msea.R

@Composable
fun ListArrowForward() {
    Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
        tint = Color.LightGray,
        contentDescription = null
    )
}