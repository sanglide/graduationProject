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
    public ResponseVO ps(String path_real,String path_cartoon){
//
//        String property_1 = System.getProperty("user.dir");
//        String savePath = property_1 + "\\src\\main\\resources\\static\\photos\\";

        //String savePath = "D:\\new\\";//图片存放位置
        // 参数说明
        // type ： opencv和baidu 两种获取人脸标记的位置点
        // jingxi : true 使用全部点位进行分割，false使用外部轮廓的点位进行融合

         path_real = "D:\\new\\ertong1.jpg";
         //path_cartoon= "D:\\new\\11.jpg";
        String savePath = "D:\\new\\";//图片存放位置
        String property = System.getProperty("user.dir");
         path_cartoon = property + "\\src\\main\\resources\\static\\photos\\gongzhu.jpg";

         System.out.println(path_cartoon);
        openCVFaceSwap.faceMerge(path_real,path_cartoon,savePath,"opencv",false);

        return ResponseVO.buildSuccess();
    }


    @Override
    public ResponseVO fix(String path_real,String path_cartoon){
        return ResponseVO.buildSuccess();
    }


}


