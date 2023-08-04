package com.flyingpig.reptile.controller;

import com.flyingpig.reptile.pojo.FzuNews;
import com.flyingpig.reptile.pojo.Result;
import com.flyingpig.reptile.service.FzuNewsService;
import com.sun.net.httpserver.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FzuNewsController {
    @Autowired
    public FzuNewsService fzuNewsService;
    @GetMapping("/addFzuNews")
    public String addFzuNewsByNewsBeginTimeAndNewsEndTime()throws Exception{
        fzuNewsService.addFzuNewsByNewsBeginTimeAndNewsEndTime("2023-01-01","2023-02-01");
        return "add success";
    }

}
