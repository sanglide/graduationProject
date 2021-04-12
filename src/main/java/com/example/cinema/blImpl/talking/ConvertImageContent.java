package com.example.cinema.blImpl.talking;

import com.example.cinema.po.ImageContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConvertImageContent {
    /**这个对象将一个字符串的list转换为imageContent对象*/
    List<String> info;
    public void setInfo(List<String> info){
        this.info=info;
    }
    public ImageContent convertImage(){
        ImageContent imageContent=new ImageContent();
        //todo:
        return imageContent;
    }

    public void main(String[] args){
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
        ImageContent imageContent=convertImageContent.convertImage();
        imageContent.printContent();
    }
}
