package com.example.cinema.controller.picture;


import com.example.cinema.bl.picture.CartoonService;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartoonController {

    @Autowired
    CartoonService cartoonService;

    @GetMapping(value = "/cartoon/ps")
    public ResponseVO ps(){
        String property_1 = System.getProperty("user.dir");
        String path_real = property_1 + "\\src\\main\\resources\\static\\photos\\ertong1.jpg";
        String path_cartoon=property_1 + "\\src\\main\\resources\\static\\photos\\gongzhu.jpg";
        return cartoonService.ps(path_real,path_cartoon);
    }


    @GetMapping(value = "/cartoon/fix")
    public ResponseVO fix(){
        String path_real="";
        String path_cartoon="";
        return cartoonService.fix(path_real,path_cartoon);
    }

}
