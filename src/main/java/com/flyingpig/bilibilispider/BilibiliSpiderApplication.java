package com.flyingpig.bilibilispider;

import com.flyingpig.bilibilispider.task.BilibiliSpiderTask;
import com.flyingpig.bilibilispider.task.DataAnalysisTask;
import com.flyingpig.bilibilispider.task.ExcelWriteTask;
import com.flyingpig.bilibilispider.util.WordCloudUtil;
import lombok.extern.slf4j.Slf4j;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Map;


@Slf4j
public class BilibiliSpiderApplication {


    public static void main(String[] args) throws ParserConfigurationException {
        log.info("爬取启动！！！");

        // 搜索2024巴黎奥运会的视频，并获取视频的cid，封装成视频的cid集合
        List<Long> cidList = BilibiliSpiderTask.SearchVideoCidListByKeyWord("2024巴黎奥运会");
        // 通过CID集合获取弹幕集合，并写入到文件中
        BilibiliSpiderTask.SearchBarrageListByCidList(cidList);
        // 统计AI技术应用方面的每种弹幕数量，并输出数量排名前8的弹幕，返回弹幕统计
        Map<String, Integer> wordCountMap = DataAnalysisTask.getTop8BarrageListAboutAI();
        // 将统计处的数据写入excel中
        ExcelWriteTask.writeBarrageListToExcel(wordCountMap);
        // 生成词云
        WordCloudUtil.generateWordCloud(wordCountMap);

        log.info("爬取结束！！！");
    }

}
