package com.example.cinema.blImpl.picture;

import com.example.cinema.bl.picture.CartoonService;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CartoonServiceImpl implements CartoonService {
    @Autowired
    OpenCVFaceSwap openCVFaceSwap;



    public static String savePath ;

    @Override
    public ResponseVO ps(String path_real, String path_cartoon){
        try{
            String property_1 = System.getProperty("user.dir");
            String savePath = property_1 + "\\src\\main\\resources\\static\\photos\\";

            System.out.println(path_cartoon);
            openCVFaceSwap.faceMerge(path_real,path_cartoon,savePath,"opencv",false);

            String result_path=property_1+"\\src\\main\\resources\\static\\photos\\result_ps.jpg";
            System.out.println(result_path);

            return ResponseVO.buildSuccess("result_ps.jpg");

        }catch (Exception e){
            System.out.println(e.toString());
            return ResponseVO.buildFailure("直接ps卡通脸失败");
        }
    }


    @Override
    public ResponseVO fix(String path_real, String path_cartoon){
        try{

            String property_1 = System.getProperty("user.dir");
            String savePath = property_1 + "\\src\\main\\resources\\static\\photos\\";

            System.out.println(path_cartoon);
            openCVFaceSwap.faceMerge(path_real,path_cartoon,savePath,"opencv",true);

            String result_path=property_1+"\\src\\main\\resources\\static\\photos\\result_fix.jpg";
            System.out.println(result_path);


            return ResponseVO.buildSuccess("result_fix.jpg");

        }catch(Exception e){
            System.out.println(e.toString());
            return ResponseVO.buildFailure("人脸和卡通人脸融合失败");
        }
    }


}


