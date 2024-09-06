package com.flyingpig.bilibilispider.util;

import com.flyingpig.bilibilispider.constant.HeaderConstant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestUtil {

    public static String requesttToGetBodyString(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", HeaderConstant.COOKIE)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] requestToGetBodyBytes(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", HeaderConstant.COOKIE)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().bytes();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

}
