package com.example.cinema.controller.talking;

import com.example.cinema.bl.talking.GenerateImages;
import com.example.cinema.bl.talking.TalkingService;
import com.example.cinema.blImpl.talking.ConvertImageContent;
import com.example.cinema.blImpl.talking.GenerateImageImlp;
import com.example.cinema.blImpl.talking.NlpRequest;
import com.example.cinema.blImpl.talking.UploadImageImlp;
import com.example.cinema.po.ImageContent;
import com.example.cinema.vo.ResponseVO;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController()
public class TalkingController {

    @Autowired
    private TalkingService talkingService;
    GenerateImages generateImages=new GenerateImageImlp();

    String base = "./video/";

    @PostMapping("talking/upload")
    public String uploadFile(@RequestParam(value="multipartFile",required = false) MultipartFile multipartFile,
                             @RequestParam(value="background",required = false) String background,
                             @RequestParam(value="pageName",required = false) String pageName,
                                     HttpServletRequest request) throws IOException, InterruptedException {
        if (multipartFile==null) {
            System.out.println("multipartFile是null");

        }
        System.out.println(multipartFile.getContentType());
        System.out.println(pageName);
        System.out.println(background);
        System.out.println("获得了文件");
        System.out.println(multipartFile.getOriginalFilename());
        System.out.println(multipartFile.getSize());
//        multipartFile.transferTo(new File(base+"music.wav"));

        /**生成音频文件并存储在本地*/
        File file=new File(base+"music.wav");
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);

        /**将音频文件转码成文字*/
        //todo:这里调试的时候改掉注释
//        String result1=talkingService.wavToString();
//        NlpRequest nlpRequest=new NlpRequest();
//        String result=nlpRequest.getWordShow(result1);
//        List<String> imageList=nlpRequest.getImageList(result);
//        ConvertImageContent convertImageContent=new ConvertImageContent(background);
//        String HashfileName=convertImageContent.convertImage(imageList);

        String result="为了节省免费服务器，就暂时关掉了，\n" +
                "如需要使用语音转文字就将TalkingController的49行注释去掉";
        List<String> info2=new ArrayList<String>(Arrays.asList("妈妈", "小女孩","酒","蛋糕"));
        ConvertImageContent convertImageContent=new ConvertImageContent(background);
        String HashfileName=convertImageContent.convertImage(info2);

        return result+"&"+HashfileName;
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
