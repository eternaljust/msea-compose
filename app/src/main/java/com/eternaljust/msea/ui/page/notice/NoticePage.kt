package com.eternaljust.msea.ui.page.notice

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NoticePage(padding: PaddingValues) {
    Surface(modifier = Modifier.padding(padding)) {
        Text(text = "通知")
    }
}