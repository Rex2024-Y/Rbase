package com.zg.baselibrary.net

class HttpResult<T> {
    var code: Int = -1
    var msg: String = ""
    var data: T? = null
}
