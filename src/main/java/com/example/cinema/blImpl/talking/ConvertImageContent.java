package com.example.cinema.blImpl.talking;

import com.example.cinema.po.ImageContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConvertImageContent {
    /**这个对象将一个字符串的list转换为imageContent对象*/
    List<String> info;
    Map<String,String> image;
    public void setInfo(List<String> info){
        this.info=info;
    }
    public void convertImage(){
        ImageContent imageContent=new ImageContent();
        //将本对象的info这个list挨个查找，
        // 并设置image Content的内容，最终形成一个能用的imageContent
        for(int i=0;i<info.size();i++){
            String temp=info.get(i);
            //todo:查找数据库里面有没有temp这个图片，
            // 如果没有，跳过；如果有，获取它的类型。存进相应的类型中，并存储图片所在哈希值

        }
    }

    public void main(String[] args){
        /**这个函数就是输入一个字符串，生成一张图片*/
        List<String> info1=new ArrayList<String>(Arrays.asList("o1", "o2"));
        List<String> info2=new ArrayList<String>(Arrays.asList("o1", "o2"));
        List<String> info3=new ArrayList<String>(Arrays.asList("o1", "o2"));
        List<String> info4=new ArrayList<String>(Arrays.asList("o1", "o2"));
        List<String> info5=new ArrayList<String>(Arrays.asList("o1", "o2"));
        List<String> info6=new ArrayList<String>(Arrays.asList("o1", "o2"));
        List<String> info7=new ArrayList<String>(Arrays.asList("o1", "o2"));
        List<String> info8=new ArrayList<String>(Arrays.asList("o1", "o2"));

        ConvertImageContent convertImageContent=new ConvertImageContent();
        convertImageContent.setInfo(info1);
        convertImageContent.convertImage();
    }
}
