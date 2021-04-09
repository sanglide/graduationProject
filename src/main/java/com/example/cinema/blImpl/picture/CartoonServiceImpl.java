package com.example.cinema.blImpl.picture;

import com.example.cinema.bl.picture.CartoonService;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;


@Service
public class CartoonServiceImpl implements CartoonService {
    @Autowired
    OpenCVFaceSwap openCVFaceSwap;



    public static String savePath ;

    @Override
    public ResponseVO ps(String path_real, String path_cartoon, HttpServletResponse rp){
        String property_1 = System.getProperty("user.dir");
        String savePath = property_1 + "\\src\\main\\resources\\static\\photos\\";

        openCVFaceSwap.faceMerge(path_real,path_cartoon,savePath,"opencv",false);

        String result_path=property_1+"\\src\\main\\resources\\static\\photos\\result.jpg";
        openCVFaceSwap.uploadPictureToAdvice(result_path,rp);

        return ResponseVO.buildSuccess();
    }


    @Override
    public ResponseVO fix(String path_real,String path_cartoon,HttpServletResponse rp){
        String property_1 = System.getProperty("user.dir");
        String savePath = property_1 + "\\src\\main\\resources\\static\\photos\\";

        openCVFaceSwap.faceMerge(path_real,path_cartoon,savePath,"opencv",true);

        String result_path=property_1+"\\src\\main\\resources\\static\\photos\\result.jpg";
        openCVFaceSwap.uploadPictureToAdvice(result_path,rp);


        return ResponseVO.buildSuccess();
    }


}


