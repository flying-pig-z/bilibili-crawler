package com.flyingpig.reptile.utils;

import com.flyingpig.reptile.pojo.FzuNews;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {
    //爬取福大通知、文件系统
    public List<FzuNews> crawlFzuNotificationsAndFileSystems(String newsBeginTime, String newsEndTime)throws Exception{
        boolean judge=true;
        //登录哪一页
        Integer pageNumber=0;
        List<FzuNews> fzuNewsList=new ArrayList<>();//新闻列表
        while(judge){
            //自动翻页
            pageNumber=pageNumber+1;
            //链接所需的url和cookie
            String homePageUrl="https://info22-443.webvpn.fzu.edu.cn/lm_list.jsp?totalpage=948&PAGENUM="+pageNumber.toString()+"&urltype=tree.TreeTempUrl&wbtreeid=1460";
            String cookie="_ga=GA1.3.275168140.1666703559; _gscu_1331749010=69085463ebn85988; JSESSIONID=749071D32D59113D4E6468918CFAECAC; _webvpn_key=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiMTAyMjAxNjA0IiwiZ3JvdXBzIjpbNl0sImlhdCI6MTY5MTA3NjcxMiwiZXhwIjoxNjkxMTYzMTEyfQ.HXyfVBJGujr1zsuYgimuwfMfQrS-v_K182UMjh-F8f8; webvpn_username=102201604|1691076712|0a3bf2ec9ff4eff31cf00dae990008b66a19b32c";
            //主页连接
            Connection homePageConnection = Jsoup.connect(homePageUrl)
                    .header("Cookie", cookie);  // 添加标头;
            //获得主页的html页面
            Document homePageDocument = homePageConnection.get();
            //锁定主页中的新闻部分
            Elements news=homePageDocument.getElementsByClass("clearfloat");
            //遍历新闻部分，获取各条新闻发布时间，作者，标题以及正文
            for(Element element : news){
                String time=element.getElementsByTag("span").eq(0).text();
                //如果时间太古老，就退出主循环停止爬取
                if(time.compareTo(newsBeginTime)<0) {
                    judge = false;
                    break;
                }
                //如果时间太现代，那么就不用封装数据
                if(time.compareTo(newsEndTime)>0)
                    continue;
                String author=element.getElementsByTag("a").eq(0).text();
                String title=element.getElementsByTag("a").eq(1).text();
                String textHerf="https://info22-443.webvpn.fzu.edu.cn/"+element.getElementsByTag("a").get(1).attr("href");
                String text=new HtmlParseUtil().getTextByTextHerf(textHerf);
                //封装获得的数据
                FzuNews fzuNews=new FzuNews();
                fzuNews.setTime(time);
                fzuNews.setAuthor(author);
                fzuNews.setTitle(title);
                fzuNews.setText(text);
                fzuNewsList.add(fzuNews);
                System.out.println(time+"\\"+judge);
            }
        }

        return fzuNewsList;
    }
    //通过URL获取正文
    public String getTextByTextHerf(String textHerf)throws Exception{
        String textResult=new String();//最终的正文
        //正文页面的请求
        Connection textConnection = Jsoup.connect(textHerf)
                .header("Cookie", "_ga=GA1.3.275168140.1666703559; _gscu_1331749010=69085463ebn85988; JSESSIONID=749071D32D59113D4E6468918CFAECAC; _webvpn_key=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiMTAyMjAxNjA0IiwiZ3JvdXBzIjpbNl0sImlhdCI6MTY5MTA3NjcxMiwiZXhwIjoxNjkxMTYzMTEyfQ.HXyfVBJGujr1zsuYgimuwfMfQrS-v_K182UMjh-F8f8; webvpn_username=102201604|1691076712|0a3bf2ec9ff4eff31cf00dae990008b66a19b32c");
        //获得正文的html页面
        Document document = textConnection.get();
        //锁定正文的元素并获取正文
        Elements elements=document.getElementsByTag("p");
        for(Element element:elements){
            textResult=textResult+element.text()+"\n";
        }
        //返回结果
        return textResult;
    }

}
