# Msea Compose 依赖升级迁移计划

## 升级进度概览

| 阶段 | 状态 | 说明 |
|------|------|------|
| 阶段 1 | ✅ 完成 | Gradle 配置与 Kotlin 升级 |
| 阶段 2 | ✅ 完成 | Jetpack 核心库升级 |
| 阶段 3 | ⏳ 待执行 | Accompanist 迁移 |
| 阶段 4 | ⏳ 待执行 | 第三方库升级 |
| 阶段 5 | ⏳ 可选 | 大版本迁移 (Coil 3 / OkHttp 5) |

---

## 已完成的升级

### 阶段 1：Gradle 配置与 Kotlin 升级

- ✅ Kotlin: 2.1.20 → 2.3.0 (用户已升级到 2.3.20)
- ✅ AGP: 8.13.2 (用户已升级到 9.1.0)
- ✅ 引入 Compose BOM: `androidx.compose:compose-bom:2026.01.00`
- ✅ 移除 `composeOptions.kotlinCompilerExtensionVersion`（Kotlin 2.0+ 不再需要）
- ✅ 添加 `material-icons-extended` 依赖

### 阶段 2：Jetpack 核心库升级

| 库 | 旧版本 | 新版本 |
|---|--------|--------|
| Lifecycle | 2.8.7 | 2.10.0 |
| Navigation | 2.8.9 | 2.9.7 |
| Activity Compose | 1.10.1 | 1.13.0 |
| DataStore | 1.1.4 | 1.1.7 |
| Paging | 3.3.6 | 3.4.0 |

---

## 阶段 3：Accompanist 迁移（待执行）

### 3.1 迁移 Pager → Compose 原生 API

Accompanist Pager 已废弃，需迁移到 Compose Foundation 原生 Pager。

#### 需要修改的文件（7 个）：

1. `ui/page/home/HomePage.kt`
2. `ui/page/home/search/SearchPage.kt`
3. `ui/page/home/sign/SignPage.kt`
4. `ui/page/notice/NoticePage.kt`
5. `ui/page/profile/detail/ProfileDetailPage.kt`
6. `ui/page/profile/detail/ProfileCreditPage.kt`
7. `ui/page/profile/detail/ProfileFriendPage.kt`

#### 迁移步骤：

**旧 API (Accompanist):**
```kotlin
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
val pagerState = rememberPagerState()

HorizontalPager(
    count = items.size,
    state = pagerState
) { page ->
    // content
}
```

**新 API (Compose Foundation):**
```kotlin
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

val pagerState = rememberPagerState { items.size }

HorizontalPager(
    state = pagerState
) { page ->
    // content
}
```

**关键变更：**
- `count` 参数移到 `rememberPagerState { count }` 的 lambda 中
- 移除 `@OptIn(ExperimentalPagerApi::class)`
- `PagerScope` 变为直接使用 `page` 索引

### 3.2 迁移 SwipeRefresh → Material3 PullToRefreshBox

#### 需要修改的文件（4 个）：

1. `ui/widget/RefreshList.kt` - 核心组件
2. `ui/page/home/topic/TopicDetailPage.kt`
3. `ui/page/home/topic/TopicListPage.kt`
4. `ui/page/home/search/SearchPostPage.kt`

#### 迁移步骤：

**旧 API (Accompanist):**
```kotlin
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

SwipeRefresh(
    state = swipeRefreshState,
    onRefresh = { /* refresh logic */ },
    indicator = { state, refreshTrigger ->
        SwipeRefreshIndicator(state, refreshTrigger)
    }
) {
    // content
}
```

**新 API (Material3):**
```kotlin
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

var isRefreshing by remember { mutableStateOf(false) }

PullToRefreshBox(
    isRefreshing = isRefreshing,
    onRefresh = { /* refresh logic */ }
) {
    // content
}
```

**关键变更：**
- `SwipeRefreshState` → `isRefreshing: Boolean` 状态
- `SwipeRefresh` → `PullToRefreshBox`
- 移除 `indicator` 自定义，使用 Material3 默认样式

### 3.3 RefreshList.kt 核心组件迁移

这是最重要的迁移，需要重构 `RefreshList`、`RefreshListState`、`RefreshGrid` 三个函数：

```kotlin
// 新版 RefreshList 实现
@Composable
fun <T : Any> RefreshList(
    lazyPagingItems: LazyPagingItems<T>,
    noMoreDataText: String = "没有更多了",
    tint: Color = MaterialTheme.colorScheme.primary,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit) = {},
    itemContent: LazyListScope.() -> Unit
) {
    var refreshing by remember { mutableStateOf(false) }
    
    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = {
            onRefresh.invoke()
            lazyPagingItems.refresh()
        }
    ) {
        refreshing = (lazyPagingItems.loadState.refresh is LoadState.Loading) || isRefreshing

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemContent()
            // 加载状态处理...
        }
    }
}
```

### 3.4 移除 Accompanist 依赖

完成迁移后，更新 `app/build.gradle`：

```groovy
// 移除以下依赖
// implementation "com.google.accompanist:accompanist-pager:0.36.0"
// implementation "com.google.accompanist:accompanist-swiperefresh:0.36.0"

// 保留以下依赖（webview 和 permissions 暂无官方替代）
implementation "com.google.accompanist:accompanist-webview:0.36.0"
implementation "com.google.accompanist:accompanist-permissions:0.36.0"
```

---

## 阶段 4：第三方库升级（待执行）

### 4.1 Jsoup 升级

```groovy
// 旧版本
implementation 'org.jsoup:jsoup:1.17.2'
// 新版本
implementation 'org.jsoup:jsoup:1.22.1'
```

### 4.2 友盟 SDK 升级

需访问 https://devs.umeng.com/sdk 确认最新版本。

当前版本：
```groovy
implementation 'com.umeng.umsdk:common:9.6.7'
implementation 'com.umeng.umsdk:asms:1.8.6'
implementation 'com.umeng.umsdk:apm:1.9.5'
implementation 'com.umeng.umsdk:abtest:1.0.1'
```

---

## 阶段 5：大版本迁移（可选/高风险）

### 5.1 Coil 2.x → Coil 3.x

**包名变更：** `io.coil-kt` → `io.coil-kt.coil3`

```groovy
// 旧版本
implementation 'io.coil-kt:coil-compose:2.5.0'
// 新版本
implementation 'io.coil-kt.coil3:coil-compose:3.4.0'
```

**代码变更：**
```kotlin
// 旧导入
import coil.compose.AsyncImage
// 新导入
import coil3.compose.AsyncImage
```

Coil 3 API 基本兼容，主要注意包名变更。

### 5.2 OkHttp 4.x → OkHttp 5.x

```groovy
// 旧版本
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
// 新版本
implementation 'com.squareup.okhttp3:okhttp:5.3.2'
```

**主要变更：**
- Kotlin 协程优先 API
- 部分 Java API 已移除
- CookieJar 接口可能有变更

参考：https://square.github.io/okhttp/changelogs/changelog/

---

## 废弃 API 清理清单

编译时产生的 deprecation 警告，建议在阶段 3 一并处理：

| 废弃 API | 替代 API | 文件 |
|----------|----------|------|
| `Divider()` | `HorizontalDivider()` | 多个页面 |
| `Icons.Filled.ArrowBack` | `Icons.AutoMirrored.Filled.ArrowBack` | LoginPage, AboutPage, TopAppBar, WebView |
| `TabRow()` | `PrimaryTabRow()` / `SecondaryTabRow()` | ProfileFriendPage, SettingPage |
| `LinearProgressIndicator(progress: Float)` | `LinearProgressIndicator { progress }` | WebView.kt |
| `statusBarColor` | 使用 `enableEdgeToEdge()` | Theme.kt |

---

## 验证清单

每个阶段完成后执行：

```bash
cd msea-compose
./gradlew clean
./gradlew assembleDebug
```

确保：
- [x] 阶段 1 编译通过
- [x] 阶段 2 编译通过
- [ ] 阶段 3 编译通过
- [ ] 阶段 4 编译通过
- [ ] 阶段 5 编译通过（可选）

---

## 当前 build.gradle 配置

```groovy
plugins {
    id 'com.android.application' version '9.1.0' apply false
    id 'com.android.library' version '9.1.0' apply false
    id 'org.jetbrains.kotlin.android' version '2.3.20' apply false
    id 'org.jetbrains.kotlin.plugin.compose' version '2.3.20' apply false
}

// app/build.gradle dependencies
dependencies {
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2026.01.00')

    // Jetpack
    implementation 'androidx.core:core-ktx:1.15.0'
    implementation 'androidx.core:core-splashscreen:1.0.1'
    implementation 'androidx.datastore:datastore-preferences:1.1.7'

    // Compose
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3:1.3.2'
    implementation 'androidx.compose.material:material-icons-extended'

    // Jetpack 核心
    implementation 'androidx.activity:activity-compose:1.13.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.10.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0'
    implementation 'androidx.navigation:navigation-compose:2.9.7'
    implementation 'androidx.paging:paging-runtime-ktx:3.4.0'
    implementation 'androidx.paging:paging-compose:3.4.0'

    // Accompanist (待迁移)
    implementation "com.google.accompanist:accompanist-pager:0.36.0"
    implementation "com.google.accompanist:accompanist-swiperefresh:0.36.0"
    implementation "com.google.accompanist:accompanist-webview:0.36.0"
    implementation "com.google.accompanist:accompanist-permissions:0.36.0"

    // 第三方库
    implementation 'com.google.code.gson:gson:2.11.0'
    implementation 'org.jsoup:jsoup:1.17.2'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'io.coil-kt:coil-compose:2.5.0'

    // 友盟
    implementation 'com.umeng.umsdk:common:9.6.7'
    implementation 'com.umeng.umsdk:asms:1.8.6'
    implementation 'com.umeng.umsdk:apm:1.9.5'
    implementation 'com.umeng.umsdk:abtest:1.0.1'
}
```

---

## 参考链接

- [Compose BOM 版本映射](https://developer.android.com/develop/ui/compose/bom/bom-mapping)
- [Accompanist Pager 迁移指南](https://google.github.io/accompanist/pager/#migration)
- [Material3 PullToRefresh](https://developer.android.com/reference/kotlin/androidx/compose/material3/pulltorefresh/package-summary)
- [Coil 3 迁移指南](https://coil-kt.github.io/coil/changelog/)
- [OkHttp 5 变更日志](https://square.github.io/okhttp/changelogs/changelog/)
