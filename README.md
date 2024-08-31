Used for quickly copying and creating projects
Dependency library
1. Universal adapter
   https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki
2. H5 Android mutual calling example
   https://blog.csdn.net/huweiliyi/article/details/105895759

### Technical selection

| Technical                            | Explanation                                  | Official Website                                                                    |
|--------------------------------------|----------------------------------------------|-------------------------------------------------------------------------------------|
| Mediapipe                            | Computer Vision Framework                    | https://github.com/google-ai-edge/mediapipe                                         |
| Livedata                             | Official Observable Data                     | https://developer.android.google.cn/topic/libraries/architecture/livedata?hl=zh_cn  |
| Viewmodel                            | Official Data Layer                          | https://developer.android.google.cn/topic/libraries/architecture/viewmodel?hl=zh_cn |
| Convenient encapsulation of retrofit | okhttp                                       | https://github.com/square/retrofit                                                  |
| Mmkv                                 | Kernel or Sharefreshness                     | https://github.com/Tencent/MMKV                                                     |
| Presentation                         | Kernel or Special Dialogue                   | https://developer.android.com/reference/android/app/Presentation                    |
| BaseRecyclerViewAdapter Helper       | A rapid development adapter for RecyclerView | https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki                       |

### Tool category
   The LogUtils logging tool class manages log BaseActivity and BaseFragment uniformly, and String. LogI() can be directly called
   Network request interceptor HttpLoggingInterceptor prints request logs uniformly

### Architecture
   The project page is relatively simple and adopts the convenient MVC pattern
   Model refers to the bean data layer, View refers to the Activity or Fragment Controller, and corresponds to the business layer
   VNet handle business logic and notify the View layer to update the UI through LiveData
   Fragment is not recommended for a single page

### Local cache
   MMKV local persistent lightweight storage key value
   Put get is easy to use

### Secondary screen architecture
   You can open the second screen through HDMI or other means, and the usage method is basically the same as that of a regular dialog
   ###Universal adapter
   Can be used as a substitute for various list table tabs
   https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki