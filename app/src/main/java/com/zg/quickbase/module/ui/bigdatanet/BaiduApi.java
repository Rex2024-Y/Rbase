package com.zg.quickbase.module.ui.bigdatanet;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaiduApi {
    public static final String API_KEY = "2Pgdiu3mZqcVRBlGT1y9vMPK";
    public static final String SECRET_KEY = "ecWsD95XmdbmuSiQ1lQMElTjCQs9Gl7L";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static String baiduGeneralImage(String imageBase64) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        image=iVBORw0KGgo
        RequestBody body = RequestBody.create(mediaType, imageBase64);

        Log.i("AIViewModel", "imageBase64:" + imageBase64);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body().string();
    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        try {
            return new JSONObject(response.body().string()).getString("access_token");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
