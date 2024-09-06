package com.flyingpig.bilibilispider.task;

import com.flyingpig.bilibilispider.constant.FileName;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class DataAnalysisTask {


    private static List<String> aiKeywords = new ArrayList<>();

    static {
        // 使用类加载器获取资源文件的输入流
        InputStream inputStream = DataAnalysisTask.class.getClassLoader().getResourceAsStream(FileName.KEYWORD);

        if (inputStream != null) {
            // 读取文件内容
            aiKeywords = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.toList());
        } else {
            System.err.println("Resource not found: keyword.txt");
        }

    }


    public static HashMap<String, Integer> getTop8BarrageListAboutAI() {

        System.out.println(aiKeywords.size());
        log.info("开始统计弹幕中关于AI技术应用的关键词出现次数");

        // 从文件中读取弹幕集合
        Path filePath = Paths.get(FileName.BARRAGE);
        List<String> barrageList;
        try {
            barrageList = Files.readAllLines(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        // 记录初始时间
        LocalDateTime startTime = LocalDateTime.now();

        // 初始化Map集合
        HashMap<String, Integer> wordMap = new HashMap<>();
        for (String keyword : aiKeywords) {
            wordMap.put(keyword, 0);
        }

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // 分割弹幕列表
        int chunkSize = (int) Math.ceil((double) barrageList.size() / 5);
        List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < barrageList.size(); i += chunkSize) {
            final int start = i;
            final int end = Math.min(i + chunkSize, barrageList.size());

            Future<HashMap<String, Integer>> future = executorService.submit(() -> {
                HashMap<String, Integer> localMap = new HashMap<>(wordMap);
                for (String barrage : barrageList.subList(start, end)) {
                    for (String keyword : aiKeywords) {
                        if (barrage.contains(keyword)) {
                            localMap.put(keyword, localMap.get(keyword) + 1);
                        }
                    }
                }
                return localMap;
            });

            futures.add(future);
        }

        // 合并结果
        try {
            for (Future<HashMap<String, Integer>> future : futures) {
                HashMap<String, Integer> localMap = future.get();
                for (Map.Entry<String, Integer> entry : localMap.entrySet()) {
                    wordMap.put(entry.getKey(), wordMap.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("任务执行失败", e);
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                log.error("线程池等待终止失败", e);
                Thread.currentThread().interrupt();
            }
        }

        // 将Map内容排序并获取前8个
        HashMap<String, Integer> sortedMap = wordMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(8)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        // 输出前8个关键词及其出现次数
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            log.info(entry.getKey() + " : " + entry.getValue());
        }

        log.info("统计弹幕中关于AI技术应用的关键词出现次数任务结束, 耗时: {}ms",
                Duration.between(startTime, LocalDateTime.now()).toMillis());

        return new HashMap<>(sortedMap);
    }

}
