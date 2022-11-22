package com.eternaljust.msea.ui.page.profile

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.page.profile.setting.SettingListItem
import com.eternaljust.msea.ui.page.profile.setting.SettingViewAction
import com.eternaljust.msea.ui.page.profile.setting.SettingViewEvent
import com.eternaljust.msea.ui.page.profile.setting.SettingViewModel
import com.eternaljust.msea.ui.widget.ListArrowForward
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SettingViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is SettingViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(SettingViewAction.PopBack) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = mseaTopAppBarColors()
            )
        },
        content = { paddingValues ->
            LazyColumn(contentPadding = paddingValues) {
                viewModel.itemGroups.forEach { items ->
                    items(items) { item ->
                        ListItem(
                            headlineText = { Text(text = item.title) },
                            leadingContent = { SettingListIcon(item = item) },
                            trailingContent = { ListArrowForward() }
                        )

                        if (item == items.last()) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SettingListIcon(item: SettingListItem) = when (item) {
    SettingListItem.COLOR_SCHEME ->
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Default.SettingsBrightness,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )

    SettingListItem.FEEDBACK ->
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Default.Feedback,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )

    SettingListItem.CONTACT_US ->
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Default.Contacts,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )

    SettingListItem.SHARE ->
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Default.Share,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )

    SettingListItem.CLEAN_CACHE ->
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Default.CleaningServices,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )

    SettingListItem.TERMS_OF_SERVICE ->
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Default.ViewList,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
}