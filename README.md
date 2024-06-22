用于快速复制创建的工程

依赖库
1.万能适配器
https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki
2.H5 安卓互相调用示例
https://blog.csdn.net/huweiliyi/article/details/105895759?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-1-105895759-blog-104999853.235%5Ev43%5Epc_blog_bottom_relevance_base9&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-1-105895759-blog-104999853.235%5Ev43%5Epc_blog_bottom_relevance_base9&utm_relevant_index=2

### 技术选型

| 技术                            | 说明                   | 官网                                                                                  |
|-------------------------------|----------------------|-------------------------------------------------------------------------------------|
| mediapipe                     | 计算机视觉框架              | https://github.com/google-ai-edge/mediapipe                                         |
| livedata                      | 官方可观察数据              | https://developer.android.google.cn/topic/libraries/architecture/livedata?hl=zh_cn  |
| viewmodel                     | 官方数据层                | https://developer.android.google.cn/topic/libraries/architecture/viewmodel?hl=zh_cn |
| retrofit                      | okhttp的便捷封装          | https://github.com/square/retrofit                                                  |
| mmkv                          | 内核还是shareprefrence   | https://github.com/Tencent/MMKV                                                     |
| Presentation                  | 内核还是特殊dialog         | https://developer.android.com/reference/android/app/Presentation                    |
| BaseRecyclerViewAdapterHelper | RecyclerView的快速开发适配器 | https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki                       |
| GeckoView                     | 火狐浏览器内核              | https://wiki.mozilla.org/Mobile/GeckoView                                           |

### 工具类
日志工具类 LogUtils  统一管理log  BaseActivity和BaseFragment下可以 String.LogI() 直接调用
网络请求拦截器 HttpLoggingInterceptor 统一打印请求日志

### 架构
项目页面较为简单采用便捷的MVC模式
Model 即为 bean数据层   View 即为 Activity或者Fragment  Controller即业务层  对应ViewModel
ViewModel处理业务逻辑,通过LiveData通知View层更新UI
单页面不推荐用Fragment

### 本地缓存
mmkv 本地持久化 轻量级存key value
put get 使用简单

### 副屏架构
可以通过hdmi等开启第二个屏幕，使用方法和普通dialog基本一致

### 万能适配器
用于各种列表 表格 tab均可代替
https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki


### 火狐浏览器内核
解决部分机器webview不兼容的问题
https://wiki.mozilla.org/Mobile/GeckoView