package com.flyingpig.reptile.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FzuNews {
    private  String time;
    private String author;
    private String title;
    private String text;
    //可以自己添加属性
}
