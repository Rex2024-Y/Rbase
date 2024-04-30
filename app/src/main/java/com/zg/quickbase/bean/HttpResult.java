package com.zg.quickbase.bean;

import androidx.annotation.NonNull;

import okhttp3.ResponseBody;

public class HttpResult  {
    public int responseCode;
    public String responseMsg;
    public Object data;

    @Override
    public String toString() {
        return "HttpResult{" +
                "responseCode=" + responseCode +
                ", responseMsg='" + responseMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
