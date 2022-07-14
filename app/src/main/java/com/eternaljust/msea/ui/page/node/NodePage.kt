package com.eternaljust.msea.ui.page.node

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NodePage(padding: PaddingValues) {
    Surface(modifier = Modifier.padding(padding)) {
        Text(text = "节点")
    }
}