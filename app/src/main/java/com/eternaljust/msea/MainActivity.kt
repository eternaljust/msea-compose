package com.eternaljust.msea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.eternaljust.msea.ui.page.home.HomePage
import com.eternaljust.msea.ui.page.node.NodePage
import com.eternaljust.msea.ui.page.notice.NoticePage
import com.eternaljust.msea.ui.theme.MseaComposeTheme
import kotlinx.coroutines.launch

private val bottomNavigationBarItems = listOf(
    NavigationBarItem(Icons.Default.Home, R.string.bottom_navigation_home),
    NavigationBarItem(Icons.Default.Notifications, R.string.bottom_navigation_notice),
    NavigationBarItem(Icons.Default.List, R.string.bottom_navigation_node)
)

private data class NavigationBarItem(
    val image: ImageVector,
    @StringRes val text: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MseaComposeTheme {
                MseaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MseaApp() {
    MaterialTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val items = listOf(Icons.Default.Home, Icons.Default.Notifications, Icons.Default.List)
        val selectedDrawerItem = remember { mutableStateOf(items[0]) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item, contentDescription = null) },
                        label = { Text(item.name) },
                        selected = item == selectedDrawerItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedDrawerItem.value = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            },
            content = {
                var selectedBarItem by remember { mutableStateOf(0) }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Msea") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch { drawerState.open() }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = null
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            bottomNavigationBarItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Icon(item.image, contentDescription = null) },
                                    label = { Text(stringResource(id = item.text)) },
                                    selected = selectedBarItem == index,
                                    onClick = { selectedBarItem = index }
                                )
                            }
                        }
                    },
                    content = { padding ->
                        when (selectedBarItem) {
                            0 -> HomePage(padding = padding)
                            1 -> NoticePage(padding = padding)
                            2 -> NodePage(padding = padding)
                        }
                    }
                )
            }
        )
    }
}
