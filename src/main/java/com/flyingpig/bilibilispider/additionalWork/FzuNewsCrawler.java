package com.flyingpig.bilibilispider.additionalWork;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FzuNewsCrawler {


    // 常量：URL 和 Cookie
    private static final String BASE_URL = "https://info22.fzu.edu.cn/lm_list.jsp?";
    private static final String NEWS_URL = BASE_URL + "totalpage=948&PAGENUM=%d&urltype=tree.TreeTempUrl&wbtreeid=1460";


    // 爬取福大通知和文件系统
    public List<FzuNews> crawlFzuNotificationsAndFileSystems(String newsBeginTime, String newsEndTime) throws Exception {
        List<FzuNews> fzuNewsList = new ArrayList<>(); // 保存新闻列表
        int pageNumber = 0; // 当前页码
        boolean continueCrawling = true; // 是否继续爬取标志

        FileWriter writer = new FileWriter("news.txt");

        // 循环爬取页面，直到时间范围超出设定
        while (continueCrawling) {
            pageNumber++;
            String pageUrl = String.format(NEWS_URL, pageNumber);



            try {
                // 获取当前页的文档
                Document pageDocument = fetchPage(pageUrl);
                // 获取新闻列表
                Elements newsElements = pageDocument.getElementsByClass("clearfloat");

                for (Element newsElement : newsElements) {
                    String time = newsElement.getElementsByTag("span").eq(0).text();

                    // 如果新闻时间早于指定开始时间，停止爬取
                    if (time.compareTo(newsBeginTime) < 0) {
                        continueCrawling = false;
                        break;
                    }

                    // 如果新闻时间晚于结束时间，跳过这条新闻
                    if (time.compareTo(newsEndTime) > 0) {
                        continue;
                    }

                    // 获取新闻的作者、标题和正文链接
                    String author = newsElement.getElementsByTag("a").eq(0).text();
                    String title = newsElement.getElementsByTag("a").eq(1).text();
                    String textHref = BASE_URL + newsElement.getElementsByTag("a").get(1).attr("href");
                    String text = fetchNewsText(textHref);

                    // 将新闻信息写入文件中
                    writer.write("Author: " + author + "\n");
                    writer.write("Title: " + title + "\n");
                    writer.write("Link: " + textHref + "\n");
                    writer.write("Text: " + text + "\n");
                    log.info("News written to file successfully!");

                }
            } catch (Exception e) {
                // 打印错误信息并继续爬取
                log.error("抓取或解析页面时出错: " + pageUrl + "。错误信息: " + e.getMessage());
            }
        }

        return fzuNewsList; // 返回新闻列表
    }

    // 获取指定页面的HTML内容
    private Document fetchPage(String url) throws Exception {
        return Jsoup.connect(url).get();
    }

    // 获取新闻正文内容
    private String fetchNewsText(String textHref) throws Exception {
        StringBuilder textBuilder = new StringBuilder(); // 使用StringBuilder拼接正文内容
        Document document = Jsoup.connect(textHref).get();

        // 获取所有<p>标签中的正文
        Elements paragraphs = document.getElementsByTag("p");
        for (Element paragraph : paragraphs) {
            textBuilder.append(paragraph.text()).append("\n");
        }

        return textBuilder.toString(); // 返回拼接好的正文
    }

    // FzuNews类，用于封装新闻数据
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FzuNews {
        private String time;   // 新闻发布时间
        private String author; // 新闻作者
        private String title;  // 新闻标题
        private String text;   // 新闻正文
    }
}
