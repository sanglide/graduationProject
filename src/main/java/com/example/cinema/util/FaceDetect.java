package com.example.cinema.util;

//import com.example.cinema.bean.FaceV3DetectBean;
//import com.baidu.aip.face.AipFace;
//import com.baidu.aip.util.Util;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.Facemark;
import org.bytedeco.opencv.opencv_face.FacemarkLBF;
//import org.json.JSONObject;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: FaceDetect
 * @description: 人脸检测接口返回处理方法
 * @author: 小帅丶
 * @create: 2019-05-18
 **/
public class FaceDetect {
//    private static AipFace aipFace;
//    //请填写自己应用的appid apikey secretkey
//    static {
//        aipFace = new AipFace("","","");
//    }
    static Facemark facemark = FacemarkLBF.create();
    static{
        try{
            facemark.loadModel("D:\\new\\lbfmodel.yaml");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    /**
//     * 人脸检测并返回72关键点
//     * @author 小帅丶
//     * @date 2019/5/18
//     * @param path 图片路径
//     * @return org.opencv.core.Point[]
//     **/
//    public static List<Point2f> detect(String path) {
//        List<Point2f> points;
//        try {
//            HashMap<String, String> option = new HashMap<String, String>();
//            String image = Base64Util.encode(Util.readFileByBytes(path));
//            String type = "BASE64";
//            option.put("face_field","age,beauty,expression,face_shape,gender,glasses,landmark,landmark150,race,quality,eye_status,emotion,face_type");
//            JSONObject jsonObject = aipFace.detect(image,type,option);
//            com.alibaba.fastjson.JSON object = com.alibaba.fastjson.JSON.parseObject(jsonObject.toString());
//            FaceV3DetectBean bean = com.alibaba.fastjson.JSONObject.toJavaObject(object, FaceV3DetectBean.class);
//            int k72 = bean.getResult().getFace_list().get(0).getLandmark72().size();
//            points = new ArrayList<Point2f>() ;
//            Point2f pf = new Point2f();
//            for (int i = 0; i < k72; i++) {
//                float x = bean.getResult().getFace_list().get(0).getLandmark72().get(i).getX();
//                float y = bean.getResult().getFace_list().get(0).getLandmark72().get(i).getY();
//               // System.out.println(x+"="+y);
//                points.add(new Point2f(x,y));
//            }
//            return points;
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("错误了"+e.getMessage());
//            return  null;
//        }
//    }

    public static List<Point2f> detect(Mat frame){
        Mat gray = new Mat();
        // 将视频帧转换至灰度图, 因为Face Detector的输入是灰度图
        opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
        // 存储人脸矩形框的容器
        RectVector faces = CheckFaceAndEye.findFaces(gray);
        // 人脸关键点的容器
        Point2fVectorVector landmarks = new Point2fVectorVector();
        if(faces == null){
            Assert.notNull(faces,"没有检测到人脸数据");
        }
        // 运行人脸关键点检测器（landmark detector）
        boolean success = facemark.fit(frame, faces, landmarks);
        Assert.isTrue(success,"获取人脸关键点失败");
        List<Point2f> poinst = new ArrayList<>();
        //获取一张脸的
        if(success){
            Point2fVector pf = landmarks.get(0);
            for(Point2f f : pf.get()){
                poinst.add(new Point2f(f.x()-3,f.y()));
            }
            return  poinst;
        }
        return  null;
    }
}
