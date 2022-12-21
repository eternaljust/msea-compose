package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.widget.NormalTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    uid: String,
    viewModel: ProfileDetailViewModel = viewModel()
) {
    viewModel.dispatch(ProfileDetailViewAction.SetUid(uid = uid))
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is ProfileDetailViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "个人空间",
                onClick = { viewModel.dispatch(ProfileDetailViewAction.PopBack) }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Column {
                    ProfileDetailHeader(profile = viewModel.viewStates.profile)
                }
            }
        }
    )
}

@Composable
fun ProfileDetailHeader(
    profile: ProfileDetailModel
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(200.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        AsyncImage(
            modifier = Modifier
                .size(80.dp)
                .clip(shape = RoundedCornerShape(6)),
            model = profile.avatar,
            placeholder = painterResource(id = R.drawable.icon),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .offset(y = 10.dp),
            text = "${profile.name} uid(${profile.uid})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        val level = if (profile.level.contains("(")) {
            profile.level
        } else {
            "用户组(${profile.level})"
        }
        Text(
            modifier = Modifier
                .offset(y = 10.dp),
            text = level,
            color = MaterialTheme.colorScheme.secondary
        )

        Row(
            modifier = Modifier
                .offset(y = 10.dp)
        ) {
            Text(
                text = "好友: "
            )

            Text(
                text = profile.friend,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " 回帖: "
            )

            Text(
                text = profile.reply,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " 主题: "
            )

            Text(
                text = profile.topic,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier
                .offset(y = 10.dp)
        ) {
            Text(
                text = "积分: "
            )

            Text(
                text = profile.integral,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " Bit: "
            )

            Text(
                text = profile.bits,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " 违规: "
            )

            Text(
                text = profile.violation,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}