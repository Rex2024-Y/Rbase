package com.zg.quickbase.net.service;


import com.zg.quickbase.bean.HttpResult;
import com.zg.quickbase.request.LoginParam;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 用来测试的两个在线接口
 */
public interface Mainservice {

    @POST("login")
    Call<HttpResult> login(@Body LoginParam loginParam);

    @Headers("Hpep:6")
    @GET("adminRolePermissionList")
    Call<HttpResult> getQuery(@Query("id") int id);


    /**
     * 下载新apk
     */
    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> download(@Url String url);
}