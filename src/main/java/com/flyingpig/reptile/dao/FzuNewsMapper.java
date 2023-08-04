package com.flyingpig.reptile.dao;


import com.flyingpig.reptile.pojo.FzuNews;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FzuNewsMapper {
    public void addFzuNewsList(FzuNews fzuNews) ;
}
