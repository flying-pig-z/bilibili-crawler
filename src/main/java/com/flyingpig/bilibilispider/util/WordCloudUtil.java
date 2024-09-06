package com.flyingpig.bilibilispider.util;

import com.flyingpig.bilibilispider.constant.FileName;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    词云生成器
 */
public class WordCloudUtil {


    public static void generateWordCloud(Map<String, Integer> wordCountMap) {
        // 生成词云
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        List<WordFrequency> wordFrequencies = new ArrayList<>();

        // 用词语来随机生成词云
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            wordFrequencies.add(new WordFrequency(entry.getKey(), entry.getValue()));
        }
        //加入分词并随机生成权重，每次生成得图片都不一样
        //test.stream().forEach(e-> wordFrequencies.add(new WordFrequency(e,new Random().nextInt(test.size()))));
        //此处不设置会出现中文乱码
        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);
        //设置图片分辨率
        Dimension dimension = new Dimension(500, 500);
        //此处的设置采用内置常量即可，生成词云对象
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        //设置边界及字体
        wordCloud.setPadding(2);
        //因为我这边是生成一个圆形,这边设置圆的半径
        wordCloud.setBackground(new CircleBackground(255));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 42));
        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setKumoFont(new KumoFont(font));
        wordCloud.setBackgroundColor(new Color(255, 255, 255));
        // 这边是生成一个圆形,这边设置圆的半径
        wordCloud.build(wordFrequencies);
        //生成词云图路径
        wordCloud.writeToFile(FileName.WORDCLOUD);

    }
}
