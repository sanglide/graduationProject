package com.example.cinema.controller.talking;

import com.example.cinema.bl.talking.GenerateImages;
import com.example.cinema.bl.talking.TalkingService;
import com.example.cinema.blImpl.talking.GenerateImageImlp;
import com.example.cinema.po.ImageContent;
import com.example.cinema.vo.ResponseVO;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController()
public class TalkingController {

    @Autowired
    private TalkingService talkingService;
    GenerateImages generateImages=new GenerateImageImlp();

    String base = "./video/";

    @PostMapping("talking/upload")
    public String uploadFile(@RequestParam(value="multipartFile",required = false) MultipartFile multipartFile, HttpServletRequest request) throws IOException, InterruptedException {
        if (multipartFile==null) {
            System.out.println("multipartFile是null");

        }
        System.out.println(multipartFile.getContentType());
        System.out.println("获得了文件");
        System.out.println(multipartFile.getOriginalFilename());
        System.out.println(multipartFile.getSize());
//        multipartFile.transferTo(new File(base+"music.wav"));
        File file=new File(base+"music.wav");
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);


        generateImages.generateImage(new ImageContent());
        System.out.println("结束生成图片");

        String result=talkingService.wavToString();
        return result;
    }

    @PostMapping("talking/uploadURL")
    public String uploadURL(@RequestParam String url) throws IOException {

        System.out.println(url);
        return "success";
    }

    @GetMapping("talking/uploadImage")
    public ResponseVO getHot(){
        ResponseVO re=generateImages.getImageRe();
        File file= (File) re.getContent();
        System.out.println(file.length());
        return re;
    }
}
