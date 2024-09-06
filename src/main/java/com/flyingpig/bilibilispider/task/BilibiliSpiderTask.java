package com.flyingpig.bilibilispider.task;

import com.flyingpig.bilibilispider.constant.FileName;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import static com.flyingpig.bilibilispider.constant.UrlConstant.*;
import static com.flyingpig.bilibilispider.util.RequestUtil.requestToGetBodyBytes;
import static com.flyingpig.bilibilispider.util.RequestUtil.requesttToGetBodyString;

@Slf4j
public class BilibiliSpiderTask {

    public static List<Long> SearchVideoCidListByKeyWord(String keyword) {
        log.info("搜索获得cid任务开始");

        List<Long> cidList = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(6);

        try {
            List<Future<List<Long>>> futures = new ArrayList<>();
            for (int j = 1; j <= 6; j++) {
                final int page = j;
                Future<List<Long>> future = executor.submit(() -> {
                    List<Long> pageCidList = new ArrayList<>();

                    String searchUrl = HttpUrl.parse(BILIBILI_SEARCH_URL).newBuilder()
                            .addQueryParameter("keyword", keyword)
                            .addQueryParameter("search_type", "video")
                            .addQueryParameter("page", String.valueOf(page))
                            .addQueryParameter("page_size", String.valueOf(50))
                            .build().toString();

                    log.info("爬取第 {} 页", page);

                    JsonArray searchResultArray = JsonParser.parseString(requesttToGetBodyString(searchUrl))
                            .getAsJsonObject().getAsJsonObject("data")
                            .getAsJsonArray("result");

                    for (int i = 0; i < searchResultArray.size(); i++) {
                        String bvid = searchResultArray.get(i).getAsJsonObject().get("bvid").getAsString();
                        log.info("视频bvid: {}", bvid);

                        String getCidUrl = HttpUrl.parse(BILIBILI_GETCID_URL).newBuilder()
                                .addQueryParameter("bvid", bvid)
                                .addQueryParameter("jsonp", "jsonp")
                                .build()
                                .toString();

                        Long cid = JsonParser.parseString(requesttToGetBodyString(getCidUrl))
                                .getAsJsonObject().getAsJsonArray("data")
                                .get(0).getAsJsonObject().get("cid").getAsLong();
                        pageCidList.add(cid);

                        log.info("视频cid: {}", cid);
                    }
                    return pageCidList;
                });
                futures.add(future);
            }

            for (Future<List<Long>> future : futures) {
                try {
                    cidList.addAll(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    log.error("获取cid时发生错误", e);
                }
            }

        } finally {
            executor.shutdown();
        }

        log.info("搜索任务结束");
        return cidList;
    }


    public static void SearchBarrageListByCidList(List<Long> cidList) {
        log.info("爬取弹幕任务开始");
        String fileName = FileName.BARRAGE;
        // 先删除之前的弹幕文件
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        // 创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (Long cid : cidList) {
            executorService.submit(() -> {
                try {
                    byte[] bytes = requestToGetBodyBytes(DM_URL + cid + ".xml"); // 获取字节码数据
                    bytes = decompress(bytes); // 解压数据
                    List<String> barriageList = extractDTagContents(new String(bytes));
                    // 将弹幕写入文件, 如果文件不存在则创建，如果存在则追加
                    synchronized (BilibiliSpiderTask.class) {
                        try (FileWriter fileWriter = new FileWriter(fileName, true)) {
                            for (String barrage : barriageList) {
                                fileWriter.write(barrage + "\n");
                            }
                        }
                    }
                    log.info("已经爬取cid为 {} 的弹幕", cid);
                } catch (Exception e) {
                    log.error("获取弹幕数据失败", e);
                }
            });
        }

        // 关闭线程池
        executorService.shutdown();
        try {
            // 等待所有任务完成
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            log.error("线程池等待终止失败", e);
            Thread.currentThread().interrupt();
        }

        log.info("爬取弹幕任务结束");
    }



    // 解压数据
    private static byte[] decompress(byte[] data) throws IOException {
        byte[] decompressData = null;
        Inflater decompressor = new Inflater(true);
        decompressor.reset();
        decompressor.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int i = decompressor.inflate(buf);
                outputStream.write(buf, 0, i);
            }
            decompressData = outputStream.toByteArray();
        } catch (Exception e) {
        } finally {
            outputStream.close();
        }
        decompressor.end();
        return decompressData;
    }

    // 提取D标签内容
    private static List<String> extractDTagContents(String xmlContent) {
        List<String> result = new ArrayList<>();

        String regex = "<d[^>]*>(.*?)</d>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xmlContent);

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result;
    }

}





