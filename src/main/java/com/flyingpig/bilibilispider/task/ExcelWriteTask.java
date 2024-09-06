package com.flyingpig.bilibilispider.task;

import com.alibaba.excel.EasyExcel;
import com.flyingpig.bilibilispider.constant.FileName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelWriteTask {

    public static void writeBarrageListToExcel(Map<String, Integer> barrageCountMap) {

        log.info("开始将统计结果写入Excel文件");

        // 设置文件输出路径
        String fileName = FileName.WORD_COUNT;

        // 准备要写入的数据
        List<WordCount> dataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : barrageCountMap.entrySet()) {
            WordCount wordCount = new WordCount();
            wordCount.word = entry.getKey();
            wordCount.count = entry.getValue();
            dataList.add(wordCount);
        }

        // 使用EasyExcel写入Excel文件
        EasyExcel.write(fileName, WordCount.class).sheet("Sheet1").doWrite(dataList);

        log.info("统计结果写入Excel文件任务结束");

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class WordCount {
        private String word;
        private Integer count;
    }
}
