package com.eternaljust.msea.ui.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun AutosizeText(
    text: String,
    multiplierConstant: Float = 0.99f
) {
    var multiplier by remember { mutableStateOf(1f) }

    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Visible,
        style = LocalTextStyle.current.copy(
            fontSize = LocalTextStyle.current.fontSize * multiplier
        ),
        onTextLayout = {
            if (it.hasVisualOverflow) {
                multiplier *= multiplierConstant
            }
        }
    )
}