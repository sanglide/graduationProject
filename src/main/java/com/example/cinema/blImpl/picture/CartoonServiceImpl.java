package com.example.cinema.blImpl.picture;

import com.example.cinema.bl.picture.CartoonService;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CartoonServiceImpl implements CartoonService {
    @Autowired
    OpenCVFaceSwap openCVFaceSwap;


    @Autowired
    UploadPhotoImpl uploadPhoto;
   // UploadPhotoImpl uploadPhoto=new UploadPhotoImpl();

    public static String savePath ;

    @Override
    public ResponseVO ps(String path_real, String path_cartoon){
        try{
            String property_1 = System.getProperty("user.dir");
            String savePath = property_1 + "\\src\\main\\resources\\static\\photos\\";

            System.out.println(path_cartoon);
            boolean isTrue=openCVFaceSwap.faceMerge(path_real,path_cartoon,savePath,"opencv",false);

            if(isTrue==false){
                return ResponseVO.buildFailure("false");
            }
            String result_path=property_1+"\\src\\main\\resources\\static\\photos\\result_ps.jpg";
            System.out.println(result_path);
            String result=uploadPhoto.uploadFromService("result_ps.jpg");

            return ResponseVO.buildSuccess(result);

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
            boolean isTrue=openCVFaceSwap.faceMerge(path_real,path_cartoon,savePath,"opencv",true);

            if(isTrue==false){
                return ResponseVO.buildFailure("false");
            }


            String result_path=property_1+"\\src\\main\\resources\\static\\photos\\result_fix.jpg";
            System.out.println(result_path);

            String result=uploadPhoto.uploadFromService("result_fix.jpg");
            return ResponseVO.buildSuccess(result);

        }catch(Exception e){
            System.out.println(e.toString());
            return ResponseVO.buildFailure("人脸和卡通人脸融合失败");
        }
    }


}


