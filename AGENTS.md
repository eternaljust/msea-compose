# Msea Compose - 项目上下文文档

## 项目概述

**Msea Compose** 是一款虫部落搜索论坛的第三方 Android 客户端应用，使用现代 Android 开发技术栈构建。

- **项目名称**: msea-compose
- **包名**: com.eternaljust.msea
- **当前版本**: 1.1.1 (versionCode: 20)
- **最低 SDK**: 29 (Android 10)
- **目标 SDK**: 36

### 核心技术栈

| 类别 | 技术 |
|------|------|
| UI 框架 | Jetpack Compose 1.7.8 |
| 设计系统 | Material Design 3 (Material You) |
| 编程语言 | Kotlin 2.1.20 |
| 架构模式 | MVVM |
| 导航 | Navigation Compose 2.8.9 |
| 网络请求 | OkHttp 4.12.0 |
| HTML 解析 | Jsoup 1.17.2 |
| 图片加载 | Coil 2.5.0 |
| 数据存储 | DataStore Preferences |
| 分页 | Paging 3.3.6 |
| 统计分析 | 友盟 SDK |
| JSON 解析 | Gson 2.11.0 |

### 功能模块

- **首页**: 主题列表（最新回复、最新热门、最新发表）、搜索、签到
- **通知**: 互动消息、我的帖子、系统通知
- **节点**: 论坛节点导航、标签浏览
- **个人中心**: 用户资料、好友、收藏、积分、用户组、设置

---

## 项目结构

```
app/src/main/java/com/eternaljust/msea/
├── MainActivity.kt          # 主入口，导航配置，隐私政策
├── MseaApp.kt               # Application 类，友盟初始化
├── ui/
│   ├── data/                # 数据模型定义
│   │   └── JSONModel.kt     # Parcelable 数据类
│   ├── page/                # 页面组件（按功能模块组织）
│   │   ├── home/            # 首页模块
│   │   │   ├── HomePage.kt
│   │   │   ├── HomeViewModel.kt
│   │   │   ├── search/      # 搜索功能
│   │   │   ├── sign/        # 签到功能
│   │   │   └── topic/       # 主题详情
│   │   ├── node/            # 节点模块
│   │   ├── notice/          # 通知模块
│   │   └── profile/         # 个人中心模块
│   ├── theme/               # 主题配置
│   │   ├── Color.kt         # 颜色定义
│   │   ├── Theme.kt         # Material 3 主题
│   │   └── Type.kt          # 字体排版
│   └── widget/              # 通用 UI 组件
│       ├── CommonWidget.kt
│       ├── RefreshList.kt
│       ├── TopAppBar.kt
│       └── WebView.kt
└── utils/                   # 工具类
    ├── Constants.kt         # 常量定义
    ├── DataStoreUtil.kt     # 数据存储工具
    ├── JsonUtil.kt          # JSON 序列化扩展
    ├── NetworkUtil.kt       # 网络请求核心类
    ├── Paging.kt            # 分页数据源
    ├── RouteUtil.kt         # 路由名称和工具函数
    ├── SettingCacheInfo.kt  # 设置缓存
    ├── StatisticsTool.kt    # 友盟统计工具
    └── UserCacheInfo.kt     # 用户信息缓存
```

---

## 构建与运行

### 环境要求

- **JDK**: 17
- **Android Gradle Plugin**: 8.9.1
- **Gradle**: 使用 gradlew 包装器
- **NDK**: 27.0.12077973
- **Build Tools**: 36.0.0

### 构建命令

```bash
# 进入项目目录
cd msea-compose

# 清理构建
./gradlew clean

# Debug 构建
./gradlew assembleDebug

# Release 构建（启用混淆和资源压缩）
./gradlew assembleRelease

# 运行单元测试
./gradlew test

# 运行 Android 仪器测试
./gradlew connectedAndroidTest

# 安装 Debug APK 到设备
./gradlew installDebug
```

### 输出文件

Release APK 输出命名格式: `Msea_1.1.1.apk`

---

## 开发规范

### 架构约定

1. **MVVM 模式**: 每个 Page 配对一个 ViewModel
   - `XxxPage.kt` - Compose UI 组件
   - `XxxViewModel.kt` - 业务逻辑和状态管理

2. **命名规范**:
   - 页面: `*Page.kt`
   - 视图模型: `*ViewModel.kt`
   - 数据模型: `*Model.kt`
   - 工具类: `*Util.kt` 或 `*Tool.kt`

3. **路由管理**: 所有路由名称定义在 `RouteName` 对象中

### 数据存储

使用 DataStore Preferences 进行键值对存储:
- `UserInfo` - 用户登录信息和资料
- `SettingInfo` - 应用设置项

```kotlin
// 读取数据
val value = DataStoreUtil.getData(Key, defaultValue)

// 写入数据（同步）
DataStoreUtil.syncSetData(Key, value)

// 删除数据
DataStoreUtil.syncRemoveData(Key, value)
```

### 网络请求

`NetworkUtil` 封装了 OkHttp + Jsoup 的网络请求:

```kotlin
// GET 请求，返回 Jsoup Document
val document = NetworkUtil.getRequest(url)

// POST 请求
val document = NetworkUtil.postRequest(url, params)

// URL 编码
val encoded = NetworkUtil.urlEncode(param)
```

### 导航使用

```kotlin
// 跳转页面
navController.navigate(RouteName.SETTING)

// 带参数跳转
navController.navigate(RouteName.TOPIC_DETAIL + "/${topic.toJson()}")

// 获取参数
val topic = it.arguments?.getString("topic")?.fromJson<TopicDetailRouteModel>()
```

---

## 关键配置文件

| 文件 | 说明 |
|------|------|
| `build.gradle` (根目录) | 全局 Gradle 配置，依赖版本管理 |
| `app/build.gradle` | 应用模块配置，SDK 版本，依赖声明 |
| `settings.gradle` | 项目结构配置，仓库源 |
| `proguard-rules.pro` | 混淆规则 |

---

## 主题与样式

### Material You 动态配色

支持 Android 12+ 的动态颜色提取，根据壁纸自动生成配色方案：

```kotlin
MseaComposeTheme(
    darkTheme = themeStyleDark(),
    isDynamicColor = SettingInfo.instance.colorScheme
) {
    // UI content
}
```

### 主题模式

- 跟随系统 (0)
- 浅色模式 (1)  
- 深色模式 (2)

---

## 第三方服务

### 友盟统计

- 预初始化在 `MseaApp.onCreate()`
- 正式初始化在用户同意隐私政策后
- 支持云配置和 A/B 测试

### 常用 URL (HTMLURL)

基础地址: `https://www.chongbuluo.com`

主要接口:
- 登录: `/member.php?mod=logging&action=login`
- 主题列表: `/forum.php?mod=guide`
- 签到: `/plugin.php?id=wq_sign`
- 节点列表: `/forum.php?mod=index&gid=`

---

## 注意事项

1. **隐私合规**: 应用启动时需用户同意隐私政策后才会初始化友盟 SDK
2. **混淆**: Release 构建已启用 ProGuard 混淆和资源压缩
3. **ABI 过滤**: 仅支持 `arm64-v8a` 架构
4. **Cookie 管理**: 登录状态通过 `UserInfo.auth` 和 `UserInfo.salt` 维护

---

## 相关链接

- **GitHub 仓库**: https://github.com/eternaljust/msea-compose
- **Release 下载**: https://github.com/eternaljust/msea-compose/releases
- **虫部落官网**: https://www.chongbuluo.com
