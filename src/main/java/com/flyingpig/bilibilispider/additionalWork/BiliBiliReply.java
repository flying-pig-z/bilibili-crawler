package com.flyingpig.bilibilispider.additionalWork;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.HttpUrl;

import static com.flyingpig.bilibilispider.constant.UrlConstant.*;
import static com.flyingpig.bilibilispider.util.RequestUtil.requesttToGetBodyString;

public class BiliBiliReply {

    public static void main(String[] args) {

        String searchUrl = HttpUrl.parse(BILIBILI_SEARCH_URL).newBuilder()
                .addQueryParameter("keyword", "2024巴黎奥运会")
                .addQueryParameter("search_type", "video")
                .addQueryParameter("page", String.valueOf(1))
                .addQueryParameter("page_size", String.valueOf(50))
                .build().toString();


        JsonArray searchResultArray = JsonParser.parseString(requesttToGetBodyString(searchUrl))
                .getAsJsonObject().getAsJsonObject("data")
                .getAsJsonArray("result");

        for (int i = 0; i < searchResultArray.size(); i++) {
            String aid = searchResultArray.get(i).getAsJsonObject().get("aid").getAsString();

            System.out.println("正在爬取视频的aid为："+aid+"的评论");

            String getReplyUrl = HttpUrl.parse(REAPLY_URL).newBuilder()
                    .addQueryParameter("next", "1")
                    .addQueryParameter("type", "1")
                    .addQueryParameter("mode", "3")
                    .addQueryParameter("oid", aid.toString())
                    .build().toString();

            // 解析字符串为 JsonObject
            JsonObject requestObject = JsonParser.parseString(requesttToGetBodyString(getReplyUrl)).getAsJsonObject();
            if (requestObject.get("code").toString().equals("12002")) {
                continue;
            }


            JsonArray repliesArray = requestObject.getAsJsonObject("data").getAsJsonArray("replies");

            // 遍历 replies 数组
            for (JsonElement replyElement : repliesArray) {
                System.out.println(replyElement.getAsJsonObject().getAsJsonObject("content").get("message").getAsString());
            }


        }
    }

}

