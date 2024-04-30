package com.zg.quickbase.request

class LoginParam {
    var account = ""
    var password = ""
    override fun toString(): String {
        return "LoginParam(account='$account', password='$password')"
    }
}
