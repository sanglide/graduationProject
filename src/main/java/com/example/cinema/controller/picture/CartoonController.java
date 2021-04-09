package com.example.cinema.controller.picture;


import com.example.cinema.bl.picture.CartoonService;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class CartoonController {

    @Autowired
    CartoonService cartoonService;

    @GetMapping(value = "/cartoon/ps")
    public ResponseVO ps(HttpServletResponse rp){
        String property_1 = System.getProperty("user.dir");
        String path_real = property_1 + "\\src\\main\\resources\\static\\photos\\ertong1.jpg";
        String path_cartoon=property_1 + "\\src\\main\\resources\\static\\photos\\gongzhu.jpg";
        return cartoonService.ps(path_real,path_cartoon,rp);
    }


    @GetMapping(value = "/cartoon/fix")
    public ResponseVO fix(HttpServletResponse rp){
        String property_1 = System.getProperty("user.dir");
        String path_real = property_1 + "\\src\\main\\resources\\static\\photos\\ertong1.jpg";
        String path_cartoon=property_1 + "\\src\\main\\resources\\static\\photos\\gongzhu.jpg";
        return cartoonService.fix(path_real,path_cartoon,rp);
    }

}
