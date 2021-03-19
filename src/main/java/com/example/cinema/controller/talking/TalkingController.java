package com.example.cinema.controller.talking;

import com.example.cinema.bl.statistics.StatisticsService;
import com.example.cinema.bl.talking.TalkingService;
import com.example.cinema.blImpl.user.AccountServiceImpl;
import com.example.cinema.config.InterceptorConfiguration;
import com.example.cinema.vo.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@RestController()
public class TalkingController {

    @Autowired
    private TalkingService talkingService;

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

        String result=talkingService.wavToString();
        return result;
    }

    @PostMapping("talking/uploadURL")
    public String uploadURL(@RequestParam String url) throws IOException {

        System.out.println(url);
        return "success";
    }
}
