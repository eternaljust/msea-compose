# Msea Compose 依赖升级迁移计划

## 升级进度概览

| 阶段 | 状态 | 说明 |
|------|------|------|
| 阶段 1 | ✅ 完成 | Gradle 配置与 Kotlin 升级 |
| 阶段 2 | ✅ 完成 | Jetpack 核心库升级 |
| 阶段 3 | ✅ 完成 | Accompanist 迁移 |
| 阶段 4 | ✅ 完成 | 第三方库升级 |
| 阶段 5 | ✅ 完成 | 大版本迁移 (Coil 3 / OkHttp 5) |
| 阶段 6 | ✅ 完成 | Edge-to-Edge 沉浸式状态栏 + 废弃 API 清理 |

---

### 阶段 1：Gradle 配置与 Kotlin 升级

- ✅ Kotlin: 2.1.20 → 2.3.20
- ✅ AGP: 8.13.2 → 9.1.0
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

### 阶段 3：Accompanist 迁移

#### 3.1 Pager → Compose 原生 HorizontalPager

Accompanist Pager 已废弃，迁移到 Compose Foundation 原生 Pager。

需要修改的文件（7 个）：HomePage、SearchPage、SignPage、NoticePage、ProfileDetailPage、ProfileCreditPage、ProfileFriendPage。

关键变更：

- `count` 参数移到 `rememberPagerState { count }` 的 lambda 中
- 移除 `@OptIn(ExperimentalPagerApi::class)`
- 导入路径从 `com.google.accompanist.pager.*` 改为 `androidx.compose.foundation.pager.*`

#### 3.2 SwipeRefresh → Material3 PullToRefreshBox

需要修改的文件（4 个）：RefreshList、TopicDetailPage、TopicListPage、SearchPostPage。

关键变更：

- `SwipeRefreshState` 改为 `isRefreshing: Boolean` 状态
- `SwipeRefresh` 改为 `PullToRefreshBox`
- 移除 `indicator` 自定义，使用 Material3 默认样式

#### 3.3 移除 Accompanist 依赖

保留 webview 和 permissions（暂无官方替代），移除 pager 和 swiperefresh。

---

### 阶段 4：第三方库升级

| 库 | 旧版本 | 新版本 |
|---|--------|--------|
| Jsoup | 1.17.2 | 1.20.1 |
| 友盟 common | 9.6.7 | 9.9.0 |
| 友盟 asms | 1.8.6 | 1.8.7.2 |
| 友盟 apm | 1.9.5 | 2.0.8 |

---

### 阶段 5：大版本迁移 (Coil 3 / OkHttp 5)

#### 5.1 Coil 2.x → Coil 3.x

包名变更：`io.coil-kt` → `io.coil-kt.coil3`

Coil 3 默认不再支持网络图片加载，必须单独添加 `coil-network-okhttp` 依赖。

代码变更仅需修改导入路径：`coil.compose.AsyncImage` → `coil3.compose.AsyncImage`

#### 5.2 OkHttp 4.x → OkHttp 5.x

版本：4.12.0 → 5.3.2。主要变更为 Kotlin 协程优先 API。

---

### 阶段 6：Edge-to-Edge 沉浸式状态栏 + 废弃 API 清理

#### 6.1 Edge-to-Edge 适配

targetSdk 36 要求应用支持 Edge-to-Edge。

**修改内容：**

- `MainActivity.kt`：在 `onCreate` 中添加 `enableEdgeToEdge()` 调用
- `Theme.kt`：移除废弃的 `statusBarColor` 和 `isAppearanceLightStatusBars` 手动设置，`enableEdgeToEdge()` (ComponentActivity) 自动处理系统栏 insets 和图标颜色
- `AndroidManifest.xml`：添加 `android:windowSoftInputMode="adjustResize"` 配合 IME 软键盘适配

#### 6.2 TopAppBar 闪屏修复

将主页面 TopAppBar 从主 Scaffold 的 `topBar` 插槽移入各页面内部，使其参与 NavHost 页面转场动画，避免导航时绿色 TopAppBar 闪烁消失。

**修改内容：**

- `TopAppBar.kt`：新增 `MainTopAppBar` 可组合函数，支持菜单图标、标题、副标题和自定义操作按钮
- `HomePage.kt`：添加 `onMenuClick` 参数，使用 `MainTopAppBar` + 搜索/签到操作按钮
- `NoticePage.kt`：添加 `onMenuClick` 参数，使用 `MainTopAppBar`（副标题 "Make search easier"）
- `NodePage.kt`：添加 `onMenuClick` 参数，使用 `MainTopAppBar`（副标题 "让搜索更简单"）+ 标签操作按钮
- `MainActivity.kt`：移除主 Scaffold 的 `topBar` 插槽，移除 `TopAppBarAcitons` 函数，传递 `onMenuClick` 给各主页面

#### 6.3 NavHost 转场动画优化

将默认的交叉淡入淡出（crossfade）转场改为水平滑动动画，避免页面切换时出现半透明白色闪屏。

- 前进导航：新页面从右侧滑入，旧页面向左滑出
- 返回导航：前一页从左侧滑入，当前页面向右滑出

#### 6.4 DrawerHeader 颜色适配

移除 `DrawerHeader` 中的 `Surface` 包裹，使其继承 `ModalDrawerSheet` 的默认背景色，修复抽屉菜单顶部颜色不一致问题。

#### 6.5 系统栏背景常驻

在主 Scaffold 的 content 中添加状态栏和导航栏高度的常驻 Spacer，确保 Edge-to-Edge 模式下系统栏区域始终有正确的背景色。

#### 6.6 IME 软键盘适配

为包含文本输入的页面（LoginPage、SearchPage 等）添加 `consumeWindowInsets` + `imePadding()` 修饰符，确保软键盘弹出时不遮挡输入框。

#### 6.7 废弃 API 清理

| 废弃 API | 替代 API | 影响文件数 |
|----------|----------|-----------|
| `Divider()` | `HorizontalDivider()` | 5 个文件 |
| `Icons.Default.ArrowBack` | `Icons.AutoMirrored.Filled.ArrowBack` | 6 个文件 |
| `TabRow()` | `PrimaryTabRow()` | 8 个文件 |
| `LinearProgressIndicator(progress)` | `LinearProgressIndicator { progress }` | 1 个文件 |

#### 6.3 已知遗留警告

来自 Accompanist WebView（暂无官方替代方案）：

- `AccompanistWebViewClient` 相关 API 已废弃
- `WebViewState.content.getCurrentUrl()` 建议使用 `state.lastLoadedUrl`
- `webView.settings.databaseEnabled` 在 Android SDK 中已废弃

---

## 验证清单

- [x] 阶段 1 编译通过
- [x] 阶段 2 编译通过
- [x] 阶段 3 编译通过
- [x] 阶段 4 编译通过
- [x] 阶段 5 编译通过
- [x] 阶段 6 编译通过

---

## 参考

- [Compose BOM 版本映射](https://developer.android.com/develop/ui/compose/bom/bom-mapping)
- [Accompanist Pager 迁移指南](https://google.github.io/accompanist/pager/#migration)
- [Material3 PullToRefresh](https://developer.android.com/reference/kotlin/androidx/compose/material3/pulltorefresh/package-summary)
- [Coil 3 迁移指南](https://coil-kt.github.io/coil/changelog/)
- [OkHttp 5 变更日志](https://square.github.io/okhttp/changelogs/changelog/)
