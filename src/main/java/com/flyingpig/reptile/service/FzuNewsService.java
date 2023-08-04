package com.flyingpig.reptile.service;

import com.flyingpig.reptile.dao.FzuNewsMapper;
import com.flyingpig.reptile.pojo.FzuNews;
import com.flyingpig.reptile.utils.HtmlParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional(rollbackFor = Exception.class)
public class FzuNewsService{
    HtmlParseUtil htmlParseUtil=new HtmlParseUtil();
    @Autowired
    public FzuNewsMapper fzuNewsMapper;


    public void addFzuNewsByNewsBeginTimeAndNewsEndTime(String newsBeginTime,String newsEndTime)throws Exception{
        List<FzuNews> fzuNewsList=htmlParseUtil.crawlFzuNotificationsAndFileSystems(newsBeginTime,newsEndTime);
        for(FzuNews fzuNews:fzuNewsList){
            fzuNewsMapper.addFzuNewsList(fzuNews);
        }
    }

}
