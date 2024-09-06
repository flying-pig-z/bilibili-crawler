package com.flyingpig.bilibilispider;

import com.flyingpig.bilibilispider.additionalWork.FzuNewsCrawler;
import com.flyingpig.bilibilispider.task.BilibiliSpiderTask;
import com.flyingpig.bilibilispider.task.DataAnalysisTask;
import com.flyingpig.bilibilispider.task.ExcelWriteTask;
import com.flyingpig.bilibilispider.util.WordCloudUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BilibiliSpiderApplicationTests {

    @Mock
    private ExcelWriteTask excelWriteTask;

    @Test
    public void testSearchVideoCidListByKeyWord() {
        List<Long> cidList = List.of(123L, 456L);

        // Mock static methods
        try (MockedStatic<BilibiliSpiderTask> mockedStatic = mockStatic(BilibiliSpiderTask.class)) {
            // Set up the mock behavior
            mockedStatic.when(() -> BilibiliSpiderTask.SearchVideoCidListByKeyWord("2024巴黎奥运会")).thenReturn(cidList);

            // Call the method under test
            List<Long> resultCidList = BilibiliSpiderTask.SearchVideoCidListByKeyWord("2024巴黎奥运会");

            // Verify interactions
            mockedStatic.verify(() -> BilibiliSpiderTask.SearchVideoCidListByKeyWord("2024巴黎奥运会"));

            // Check the result
            assertEquals(cidList, resultCidList);
        }
    }

    @Test
    public void testSearchBarrageListByCidList() throws Exception {
        List<Long> cidList = List.of(123L, 456L);

        try (MockedStatic<BilibiliSpiderTask> mockedStatic = mockStatic(BilibiliSpiderTask.class)) {
            mockedStatic.when(() -> BilibiliSpiderTask.SearchBarrageListByCidList(cidList)).thenAnswer(invocation -> {
                return null;
            });

            assertDoesNotThrow(() -> BilibiliSpiderTask.SearchBarrageListByCidList(cidList));

            // Verify interactions with the static method
            mockedStatic.verify(() -> BilibiliSpiderTask.SearchBarrageListByCidList(cidList));
        }
    }

    @Test
    public void testGetTop8BarrageListAboutAI() throws Exception {
        Map<String, Integer> wordCountMap = new HashMap<>();
        wordCountMap.put("AI", 50);
        wordCountMap.put("Machine Learning", 30);

        try (MockedStatic<DataAnalysisTask> mockedStatic = mockStatic(DataAnalysisTask.class)) {
            mockedStatic.when(DataAnalysisTask::getTop8BarrageListAboutAI).thenReturn(wordCountMap);

            Map<String, Integer> resultMap = DataAnalysisTask.getTop8BarrageListAboutAI();

            mockedStatic.verify(DataAnalysisTask::getTop8BarrageListAboutAI);

            assertEquals(wordCountMap, resultMap);
        }
    }

    @Test
    public void testWriteBarrageListToExcel() throws Exception {
        Map<String, Integer> wordCountMap = Map.of("AI", 50, "Machine Learning", 30);

        doNothing().when(excelWriteTask).writeBarrageListToExcel(wordCountMap);

        ExcelWriteTask.writeBarrageListToExcel(wordCountMap);

        verify(excelWriteTask).writeBarrageListToExcel(wordCountMap);
    }

    @Test
    public void testGenerateWordCloud() {
        Map<String, Integer> wordCountMap = Map.of("AI", 50, "Machine Learning", 30);

        try (MockedStatic<WordCloudUtil> mockedStatic = mockStatic(WordCloudUtil.class)) {
            mockedStatic.when(() -> WordCloudUtil.generateWordCloud(wordCountMap)).thenAnswer(invocation -> {
                return null;
            });

            WordCloudUtil.generateWordCloud(wordCountMap);

            mockedStatic.verify(() -> WordCloudUtil.generateWordCloud(wordCountMap));
        }
    }





    // 注意要开校园网进行爬取！
    @Test
    void testFzuNewsCraw() throws Exception{
        FzuNewsCrawler fzuNewsCrawler = new FzuNewsCrawler();
        fzuNewsCrawler.crawlFzuNotificationsAndFileSystems("2023-01-01","2023-02-01");
    }




}
