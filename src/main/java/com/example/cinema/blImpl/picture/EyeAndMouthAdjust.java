package com.example.cinema.blImpl.picture;

import com.example.cinema.util.Correspondens;
import com.example.cinema.util.FaceDetect;
import org.apache.commons.lang3.tuple.Pair;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_photo;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.Subdiv2D;
import org.bytedeco.opencv.opencv_imgproc.Vec6fVector;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_AA;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_LINEAR;

/**
 * @description 这里使通过对融合之后的人脸进行关键点识别
 *修改关键点的坐标来达到大眼、小嘴的目的
 * @author wq
 */

@Service
public class EyeAndMouthAdjust {
    public  String savePath ;
    /**
     * @param imgPath1
     * @param path
     * @param type
     */
    public  boolean findPoints(String imgPath1,String path,String type){
        savePath = path;
        // 两张图片地址
        String path1 = imgPath1;
        // load the two images.
        Mat imgCV1 = opencv_imgcodecs.imread(path1);
        Mat imgCV2 = opencv_imgcodecs.imread(path1);
        if (null == imgCV1 || imgCV1.cols() <= 0 || null == imgCV2 || imgCV2.cols() <= 0) {
            System.out.println("There is wrong with images");
            return false;
        }

        //下面根据opencv识别出对应的关键点
        //cv::Point2f pt(10, 20);Point2f表示对应的图里面的特征点：有行和列
        List<Point2f> points12f = new ArrayList<>();
        List<Point2f> points22f = new ArrayList<>();
        points12f = FaceDetect.detect(imgCV1.clone());
        points22f = FaceDetect.detect(imgCV2.clone());
        //根据效果决定是否留存
        //---去掉多余的点位
        for(int i=0;i<68;i++){
            System.out.println(i+"   "+points22f.get(i).x()+" "+points22f.get(i).y());
        }
        // 人脸识别出的关键点
        if ("eye".equals(type)) {
            List<Point2f> temp1=eyeAdjust(points12f,points22f).getLeft();
            List<Point2f> temp2=eyeAdjust(points12f,points22f).getRight();
            points12f=temp1;
            points22f=temp2;
        }
        if ("mouth".equals(type)) {
            List<Point2f> temp1=mouthAdjust(points12f,points22f).getLeft();
            List<Point2f> temp2=mouthAdjust(points12f,points22f).getRight();
            points12f=temp1;
            points22f=temp2;
        }

        drawPoints(imgCV1.clone(),points12f,"1");
        drawPoints(imgCV2.clone(),points22f,"2");


        //----------------------三角剖分------------------------------
        Pair<Mat,Mat> wa  = toWarpAffine(imgCV1,imgCV2,points12f,points22f);
        Mat _srcImg2 = wa.getLeft();//得到三角剖分之后的图像2
        Mat srcImg1 = wa.getRight();//得到三角填充之后的图像1


        //---------------------获取图2的面部轮廓-------------------------------------
        Pair<List<Point>,List<Point2f>> hulls = getConvexHull(points22f);//找到图像2的面部轮廓
        List<Point> convexPoints2 = hulls.getLeft();
        List<Point2f> convexPoints2f = hulls.getRight();
        //---------------------图像融合-------------------------------------
        //因为经过仿射变换之后的人脸只是形状上吻合，但是边缘太生硬，需要进行图像融合
        //凸包所在的点，组成的集合其实是人脸边界轮廓
        //制作mask
        MatExpr face = Mat.zeros(imgCV2.size(), CV_8UC1);
        Mat faceMask = face.asMat();
        /**
         * 函数原型：void fillConvexPoly(Mat& img, const Point* pts, int npts, const Scalar& color, int lineType=8, int shift=0)
         * 函数作用：填充凸多边形
         * 参数说明：img                       图像
         *          pts                      指向单个多边形的指针数组
         *          npts                     多边形的顶点个数
         *          color                    多边形的颜色
         *          LineType                 组成多边形的线条的类型
         *          shift                    顶点坐标的小数点位数
         */
        opencv_imgproc.fillConvexPoly(faceMask,list2MP(convexPoints2), new Scalar(255, 255));//scalar是白色的
        //采用seamlessClone进行图像融合，效果较好，Microsoft NB的算法,无缝融合功能
        //获取最小矩形
        Rect r = opencv_imgproc.boundingRect(list2MP2(convexPoints2f));
        System.out.println(r.x()+"=="+r.y()+"=="+r.width()+"=="+r.height());

        Point center = new Point((int)(r.tl().x() + r.br().x())/2, (int)(r.tl().y() + r.br().y())/2);
        Mat resultImg = new Mat();

        /**
         * 对于cv2.seamlessClone(obj, im, mask, center, cv2.NORMAL_CLONE)来讲：
         * obj代表的是子图，由cv2读进来的数组文件；
         * im代表的是母图，也是由cv2都进来的数组文件；
         * mask代表掩模，因为你并不需要把子图所有的部分都贴进来，所以可以用mask划分出一个兴趣域。只需要用0和255区分就可以。如果你不想管这个mask，直接都设置成255就行了；
         * center表示坐标，你打算在母图的哪个位置放子图。这里是放在中间
         */
        opencv_photo.seamlessClone(_srcImg2, imgCV2, faceMask, center, resultImg,opencv_photo.NORMAL_CLONE);

        //---------------------过程图片展示-------------------------------------
        Mat[] triangleImg = new Mat[2];
        triangleImg[0] = srcImg1.clone();
        triangleImg[1] = imgCV2.clone();

        drawTriangles("3",triangleImg[1], triangles2, new Scalar(255, 255));
        drawTriangles("4",triangleImg[0], triangles1, new Scalar(255, 255));

        if(type=="eye"){
            opencv_imgcodecs.imwrite(savePath+ File.separator  + "srcImg1.jpg", srcImg1);
            opencv_imgcodecs.imwrite(savePath+ File.separator  + "srcImg2.jpg", imgCV2);

            opencv_imgcodecs.imwrite(savePath+ File.separator  + "temp.jpg", _srcImg2);
            opencv_imgcodecs.imwrite(savePath+ File.separator  + "eye_adjust.jpg", resultImg);

        }
        else{
            opencv_imgcodecs.imwrite(savePath+ File.separator  + "srcImg1.jpg", srcImg1);
            opencv_imgcodecs.imwrite(savePath+ File.separator  + "srcImg2.jpg", imgCV2);

            opencv_imgcodecs.imwrite(savePath+ File.separator  + "temp.jpg", _srcImg2);
            opencv_imgcodecs.imwrite(savePath+ File.separator  + "mouth_adjust.jpg", resultImg);

        }

        return true;

//        opencv_imgcodecs.imwrite(savePath+ File.separator  + "srcImg1.jpg", srcImg1);
//        opencv_imgcodecs.imwrite(savePath+ File.separator  + "srcImg2.jpg", imgCV2);
//
//        opencv_imgcodecs.imwrite(savePath+ File.separator  + "temp.jpg", _srcImg2);
//        opencv_imgcodecs.imwrite(savePath+ File.separator  + "result_adjust.jpg", resultImg);

    }

    /**
     * 对融合之后的图像的嘴巴进行缩小
     * 思路：
     * 1、按照opencv识别出来的嘴巴的轮廓位点48~67
     * 2、将对应的轮廓位点的坐标进行调节，从而实现缩小嘴巴的功能
     */

    public  Pair<List<Point2f>,List<Point2f>> mouthAdjust(List<Point2f> points1, List<Point2f> points2){
        List<Point2f> hull1 = new LinkedList<Point2f>();
        List<Point2f> hull2 = new LinkedList<Point2f>();

        //下面是进行对应的位点的坐标调整的x距离、y距离
        float x=0;
        float y=0;

        for(int i=0;i<68;i++){
              if(i>=48){
                  /**
                   * x0是缩小后的x坐标，x1是62的横坐标，x是当前点的横坐标
                   * y0是缩小后的y坐标，y1是48的纵坐标，y是当前点的纵坐标
                   *
                   * 以嘴唇中心点作为判断的标准
                   * 1、左边的横坐标：
                   *      x0-x=(x1-x)*0.2
                   * 2、右边的横坐标：
                   *      x-x0=(x-x1)*0.2
                   * 3、上边的纵坐标：
                   *      y-y0=(y-y1)*0.2
                   * 4、下边的纵坐标：
                   *     y0-y=(y1-y)*0.2
                   * 横坐标缩小1/5；
                   * 纵坐标缩小1/5;
                   * ===》这里可以看出来最后得到的公式之间的关系都是一样的！！！
                   */
                  y=(float)0.8*points1.get(i).y()+(float)0.2*points1.get(48).y();
                  x=(float)0.2*points1.get(62).x()+(float)0.8*points1.get(i).x();
                  hull1.add(points1.get(i));
                  hull2.add(new Point2f(x,y));
                  continue;
              }
              hull1.add(points1.get(i));
              hull2.add(points2.get(i));
        }

        return Pair.of(hull1,hull2);
    }




        /**
         * 对融合之后的图像的眼睛进行放大
         * 思路一：
         * 1、我们将后面的一张图的眼睛的关键点的坐标进行调整
         * 2、然后进行三角映射
         *
         * 思路二：
         * 1、针对之前的眼睛放大之后存在的却西安进行一定的修改
         * 2、首先将之前的两个眼睛的不动点为各自的眼睛中间的点改为===》眼睛靠近鼻梁的一侧的点
         * 3、然后每只眼睛以这个点为放大的根据，进行等比例放大
         */



    public  Pair<List<Point2f>,List<Point2f>> eyeAdjust1(List<Point2f> points1, List<Point2f> points2){

        List<Point2f> hull1 = new LinkedList<Point2f>();
        List<Point2f> hull2 = new LinkedList<Point2f>();



        /**修改的数据*/
        float y_temp=points1.get(37).y()-points1.get(41).y();
        float x_temp=points1.get(38).x()-points1.get(37).x();

        /**左眼的数据
         *           x3
         *    x1            x2
         *           x4*/
        float x1_left=points1.get(36).x();
        float x2_left=points1.get(39).x();
        float x3_left=points1.get(37).x()+(points1.get(38).x()-points1.get(37).x())/2;
        float x4_left=points1.get(41).x()+(points1.get(40).x()-points1.get(41).x())/2;
        float y1_left=points1.get(36).y();
        float y2_left=points1.get(39).y();
        float y3_left=points1.get(37).y();
        float y4_left=points1.get(41).y();

        /**右眼的数据
         *           x3
         *    x1            x2
         *           x4*/
        float x1_right=points1.get(42).x();
        float x2_right=points1.get(45).x();
        float x3_right=points1.get(43).x()+(points1.get(44).x()-points1.get(43).x())/2;
        float x4_right=points1.get(47).x()+(points1.get(46).x()-points1.get(47).x())/2;
        float y1_right=points1.get(42).y();
        float y2_right=points1.get(45).y();
        float y3_right=points1.get(43).y();
        float y4_right=points1.get(47).y();

        /**
         * 将左右眉毛的关键点存进去；
         * 同时将左右眼的关键点的数目翻倍，存进去
         */

        for(int i=17;i<=26;i++){
            hull1.add(points1.get(i));
            hull2.add(points2.get(i));
        }
        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x1_left+(x3_left-x1_left)/4*i,y1_left+(y3_left-y1_left)/4*i));
          //  hull1.add(new Point2f(x1_right+(x3_right-x1_right)/4*i,y1_right+(y3_right-y1_right)/4*i));
        }
        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x3_left+(x2_left-x3_left)/4*i,y3_left-(y3_left-y2_left)/4*i));
           // hull1.add(new Point2f(x3_right+(x2_right-x3_right)/4*i,y3_right-(y3_right-y2_right)/4*i));
        }
        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x2_left-(x2_left-x4_left)/4*i,y2_left-(y2_left-y4_left)/4*i-y_temp/2));
           // hull1.add(new Point2f(x2_right-(x2_right-x4_right)/4*i,y2_right-(y2_right-y4_right)/4*i));
        }
        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x4_left-(x4_left-x1_left)/4*i,y4_left+(y1_left-y4_left)/4*i-y_temp/2));
        }

        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x1_right+(x3_right-x1_right)/4*i,y1_right+(y3_right-y1_right)/4*i));
        }
        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x3_right+(x2_right-x3_right)/4*i,y3_right-(y3_right-y2_right)/4*i));
        }
        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x2_right-(x2_right-x4_right)/4*i,y2_right-(y2_right-y4_right)/4*i-y_temp/2));
        }
        for(int i=0;i<4;i++){
            hull1.add(new Point2f(x4_right-(x4_right-x1_right)/4*i,y4_right+(y1_right-y4_right)/4*i-y_temp/2));
        }

        for(int i=10;i<42;i++){
            if(i==10||i==26){
                hull2.add(new Point2f(hull1.get(i).x()-x_temp/2,hull1.get(i).y()));
            }
            if(i==18||i==34){
                hull2.add(new Point2f(hull1.get(i).x()+x_temp/2,hull1.get(i).y()));
            }
            if((i>=11&&i<=17)||i>=27&&i<=33){
                hull2.add(new Point2f(hull1.get(i).x(),hull1.get(i).y()+y_temp/3));
            }
            if((i>=19&&i<=25)||(i>=35&&i<=41)){
                hull2.add(new Point2f(hull1.get(i).x(),hull1.get(i).y()-y_temp/3));
            }
        }



        for(int i=0;i<68;i++){
            if((i>=17&&i<=26)||(i>=36&&i<=47)){
                continue;
            }
            hull1.add(points1.get(i));
            hull2.add(points2.get(i));
        }

        return Pair.of(hull1,hull2);
    }




    public  Pair<List<Point2f>,List<Point2f>> eyeAdjust(List<Point2f> points1, List<Point2f> points2){
        List<Point2f> hull1 = new LinkedList<Point2f>();
        List<Point2f> hull2 = new LinkedList<Point2f>();

        /**
         * 先处理左边的一只眼睛
         * 考虑将左边的眼睛的位点的坐标进行等比例的增大
         * 方法：
         * 1、就是将每两个位点之间增加一个关键点
         * 2、将对应的关键点的数目翻倍，并且将眼睛的坐标从0标起————为了后面的论文里面的图的绘画
         */


        //这里是对应的得到中间的一个关键点的xy坐标，
        // 但是41和36之间还是有一个关键位点的，47和42还有一个关键位点
        for(int i=36;i<=47;i++){
            hull1.add(points1.get(i));
            float x=0;
            float y=0;
            if(i==41){
                 x=(points1.get(41).x()+points1.get(36).x())/2;
                 y=(points1.get(41).y()+points1.get(36).y())/2;
            }
            else if(i==47){
                 x=(points1.get(42).x()+points1.get(47).x())/2;
                 y=(points1.get(42).y()+points1.get(47).y())/2;
            }
            else {
                x = (points1.get(i).x() + points1.get(i + 1).x()) / 2;
                y = (points1.get(i).y() + points1.get(i + 1).y()) / 2;
            }
            hull1.add(new Point2f(x,y));
        }
        //接下来是将对应的hull2里面的关键点的坐标，两只眼睛分别以39，42位点
        //为放大点进行放大，放大1.2倍


        /**
         * 左边的眼睛（hull1的0~11关键点）、右边的眼睛（hull2的12~23关键点）
         * 1、原本39位点的x坐标为x1，y1、原本42位点的坐标为x1,y1
         * 2、当前位点的x左边为x，y
         * 3、需要扩大之后的位点的x坐标为x0，y0
         *      ====》x1-x0=1.2*(x1-x)  ===》x0=1.2*x-0.2*x1
         *      ====》y0=1.2*y-0.2*y1
         */

        float x=0;
        float y=0;
        for(int i=0;i<24;i++){
            if(i>=0&&i<=11){
                x=(float)1.2*hull1.get(i).x()-(float)0.2*points1.get(39).x();
                y=(float)1.2*hull1.get(i).y()-(float)0.2*points1.get(39).y();
            }
            else{
                x=(float)1.2*hull1.get(i).x()-(float)0.2*points1.get(42).x();
                y=(float)1.2*hull1.get(i).y()-(float)0.2*points1.get(42).y();
            }
            hull2.add(new Point2f(x,y));
        }

        for(int i=0;i<68;i++){
            if(i>=36&&i<=47){
                continue;
            }
            hull1.add(points1.get(i));
            hull2.add(points1.get(i));
        }


        return Pair.of(hull1,hull2);
    }


        /**
         * 三角图片仿射变幻
         * 思路
         * 1、对图1进行三角剖分
         * 2、根据图像1的索引结果，得到图像2的三角剖分
         * 3、计算图像1的每个三角形到图像2对应的三角形的仿射变换矩阵
         * 4、将图1的每个三角copy到图2上面进行覆盖
         * 5、返回覆盖后的图片数据
         * @param imgCV1
         * @param imgCV2
         * @param points12f
         * @param points22f
         * @return
         */
    public  Pair<Mat,Mat> toWarpAffine(Mat imgCV1, Mat imgCV2, List<Point2f> points12f, List<Point2f> points22f){
        //----------------------三角剖分------------------------------

        //1、只需要对图1进行三角剖分即可
        // delaunay triangulation 三角剖分和仿射变换
        //这里得到的就是一个个三角形的三个顶点对应的人脸关键点的index的链表（三个为一个）的list
        Rect rect = new Rect(0, 0, imgCV1.cols(), imgCV1.rows());
        List<Correspondens> delaunayTri = delaunayTriangulation(imgCV1.clone(),points12f, rect);




        //--------------------------仿射变换-----------------------------------
        //根据图像1的索引结果，得到图像2的三角剖分（图像2不需要调用opencv的三角变换的方法）
        for(int i=0;i<delaunayTri.size();++i) {
            Correspondens corpd = delaunayTri.get(i);
            List<Point> tring = new ArrayList<>();
            tring.add(new Point((int)points22f.get(corpd.getIndex().get(0)).x(),(int)points22f.get(corpd.getIndex().get(0)).y()));
            tring.add(new Point((int)points22f.get(corpd.getIndex().get(1)).x(),(int)points22f.get(corpd.getIndex().get(1)).y()));
            tring.add(new Point((int)points22f.get(corpd.getIndex().get(2)).x(),(int)points22f.get(corpd.getIndex().get(2)).y()));
            triangles2.add(tring);
        }

        Mat _srcImg2 = imgCV2.clone();  //图像2进行复制，目的是保留原始图像
        Mat srcImg1 = imgCV1.clone();  //图像1进行复制，目的是保留原始图像

        //仿射变换
        for (int i = 0; i < triangles1.size(); i++){
            //确定ROI 计算轮廓的垂直边界最小矩形
            Rect roi_1 = opencv_imgproc.boundingRect(list2MP(triangles1.get(i)));
            Rect roi_2 = opencv_imgproc.boundingRect(list2MP(triangles2.get(i)));
            //ROI区域的图像,图像1
            Mat roi_img = new Mat(srcImg1,roi_1);
            //减去ROI左上角坐标得到 每个三角的边长
            //这里减去左上角的坐标是为了在下面的放射变换中可以变换正确！！！！
            List<Point2f> triangle1_nor = new ArrayList<>();
            List<Point2f> triangle2_nor = new ArrayList<>();
            for (Point j : triangles1.get(i)){
                float x = j.x() - roi_1.tl().x();
                float y = j.y() - roi_1.tl().y();
                triangle1_nor.add(new Point2f(x, y));
            }
            for (Point k : triangles2.get(i)){
                float x = k.x() - roi_2.tl().x()+1;  //此处加1解决拼接的图片有缝隙问题
                float y = k.y() - roi_2.tl().y()+1;
                triangle2_nor.add(new Point2f(x, y));
            }
            //计算图像1的每个三角形到图像2对应的三角形的仿射变换矩阵

            /**
             * Mat getAffineTransform( const Point2f* src, const Point2f* dst )
             * 功能：获得根据三点计算的仿射变换矩阵
             * src：输入图像的三点坐标
             * dst：输出图像的三点坐标
             */
            Mat M = opencv_imgproc.getAffineTransform(list2MP2(triangle1_nor), list2MP2(triangle2_nor));
            //仿射变换
            Mat imgWarp = new Mat();

            /**
             * void warpAffine(InputArray src, OutputArray dst, InputArray M, Size dSize,
             *  int flags = INTER_LINEAR, int borderMode = BORDER_CONSTANT, 
             *  const Scalar &borderValue = Scalar())
             * 功能：仿射变换
             * src：输入图像
             * dst：输出图像
             * M：仿射变换矩阵
             * dSize：输出图像的尺寸
             * flags ：插值算法标识符，默认为INTER_LINEAR
             *  INTER_NEAREST          ： 最邻近插值算法
             *  INTER_LINEAR              ： 线性插值算法
             *  INTER_CUBIC                ： 双立法插值算法
             *  INTER_AREA                  ： 区域插值算法
             *  INTER_LANCZOS4         ： Lanczos插值（超过8x8邻域的插值）
             *  INTER_MAX ： 用于插值的掩模板
             *  WARP_FILL_OUTLIERS ： 标志位，用于填充目标图像像素值，如果其中的    一些值对应于原图像的异常值，则这                                                 些值复位
             *  WARP_INVERSE_MAP   ：标志位，反变换
             * borderMode ：边界像素模式，默认为BORDER_CONSTANT
             *  BORDER_DEFAULT     ： 块复制
             *  BORDER_CONSTANT ： 补零
             *  BORDER_REPLICATE ： 边界复制
             *  BORDER_WRAP          ： 镜像
             * borderValue ：边界取值，默认为Scalar()，即0
             *  */
            opencv_imgproc.warpAffine(roi_img, imgWarp, M, roi_2.size(), INTER_LINEAR, BORDER_REFLECT_101,new Scalar(0,0) ); //, 1, opencv_core.BORDER_REFLECT_101
            //制作图像2的局部mask
            //MatExpr表示对Mat的某种运算
            /**
             * static cv::MatExpr cv::Mat::zeros(int rows, int cols, int type)
             * 功能：设置图像大小类型
             * rows：行数
             * cols：列数
             * type：类型
             */
            MatExpr maskExpr = Mat.zeros(roi_2.size(), CV_8U);
            Mat mask = maskExpr.asMat();
            //fillConvexPoly绘图函数输入坐标必须为int类型
            List<Point> triangle2_nor_int = new ArrayList<>();
            for (int pn=0;pn<triangle2_nor.size();pn++){
                Point2f pf = triangle2_nor.get(pn);
                triangle2_nor_int.add(new Point((int)pf.x(),(int)pf.y()));
            }
            //填充凸多边形
            //fillConvexPoly(Mat img, MatOfPoint points, Scalar color)使用opencv绘制凸折线
            opencv_imgproc.fillConvexPoly(mask,list2MP(triangle2_nor_int), new Scalar(255,255));

            Mat imageROI = new Mat(_srcImg2, roi_2);
            imgWarp.copyTo(imageROI,mask);
        }
        return Pair.of(_srcImg2,srcImg1);
    }


    //给图像画点
    public  void drawPoints( Mat face1,List<Point2f> points1,String name){
        for(int i=0;i<points1.size();i++){
            opencv_imgproc.putText(face1,i+"", new Point((int)points1.get(i).x(),(int)points1.get(i).y()), opencv_imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX,0.3, new Scalar(255, 0, 0, 0));
        }
        opencv_imgcodecs.imwrite(savePath+ File.separator + name +"_dian.jpg", face1);
    }
    //根据点给图像画线-三角形
    public  void drawTriangles(String name,Mat img,List<List<Point>> triangles, Scalar color){

        for (List<Point> points : triangles){

            opencv_imgproc.line(img, points.get(0), points.get(1), new Scalar(255, 255) , 1, CV_AA, 0);
            opencv_imgproc.line(img, points.get(1), points.get(2), new Scalar(255, 255) , 1, CV_AA, 0);
            opencv_imgproc.line(img, points.get(2), points.get(0), new Scalar(255, 255) , 1, CV_AA, 0);
        }
        opencv_imgcodecs.imwrite(savePath+ File.separator  +name +"_sanjiao.jpg", img);
    }







    /**
     * 获取Delaunay三角形的列表
     * @param hull
     * @param rect
     * @return
     */
     List<List<Point>> triangles1 = new ArrayList<>();
     List<List<Point>> triangles2 = new ArrayList<>();

    public  List<Correspondens> delaunayTriangulation(Mat img , List<Point2f> hull, Rect rect) {

        //这个是opencv三角形剖分里面的方法
        Subdiv2D subdiv = new Subdiv2D(rect);

        for(int it = 0; it < hull.size(); it++) {
            subdiv.insert(hull.get(it));
        }


        /**
         *
         * // 找到Delaunay三角形的顶点
         *     \param[out] traiangleList 三角形顶点的集合。(3个点的x座標和y座標)
         *    void getTriangleList (vector<Vec6f> & triangleList) const;
         */
        Vec6fVector triangles = new Vec6fVector();
        subdiv.getTriangleList(triangles);


        FloatPointer[] floatPs = triangles.get();

        List<Correspondens> delaunayTri = new LinkedList<Correspondens>();
        for(int i = 0; i < triangles.size(); ++i) {
            List<Point> points = new LinkedList<Point>();
            FloatPointer t = triangles.get(i);
            //获取三角行的顶点
            points.add(new Point((int) t.get(0), (int) t.get(1)));
            points.add(new Point((int)t.get(2), (int)t.get(3)));
            points.add(new Point((int)t.get(4), (int)t.get(5)));
            Correspondens ind = new Correspondens();
            //判断顶点是否在ROI矩形内
            //在Delaunay三角剖分中，选择三角形时应确保没有点位于任何三角形的外接圆之内
            if (rect.contains(points.get(0)) && rect.contains(points.get(1)) && rect.contains(points.get(2))) {

                int count = 0;
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < hull.size(); k++) {
                        if (Math.abs(points.get(j).x() - hull.get(k).x()) < 1.0 && Math.abs(points.get(j).y() - hull.get(k).y()) < 1.0) {
                            ind.add(k);
                            count++;
                        }
                    }
                }
                if (count == 3){
                    triangles1.add(points);
                    delaunayTri.add(ind);
                }

            }
        }
        return delaunayTri;
    }

    /**
     *
     * @param img1
     * @param img2
     * @param t1
     * @param t2
     * @param z
     * @return
     */
    public  Mat warpTriangle(Mat img1, Mat img2, List<Point> t1, List<Point> t2, int z) {

        Mat points1 = new Mat(t1.size());
        for(Point pf : t1){
            Mat pm = new Mat(pf);
            points1.push_back(pm);
        }
        Mat points2 = new Mat(t2.size());
        for(Point pf : t2){
            Mat pm = new Mat(pf);
            points2.push_back(pm);
        }

        Rect r1 = opencv_imgproc.boundingRect(points1);
        Rect r2 = opencv_imgproc.boundingRect(points2);

        Point[] t1Points = new Point[t1.size()];
        t1.toArray(t1Points);

        Point[] t2Points =new Point[t2.size()];
        t2.toArray(t2Points);

        List<Point2f> t1Rect = new LinkedList<Point2f>();
        List<Point2f> t2Rect = new LinkedList<Point2f>();
        List<Point> t2RectInt = new LinkedList<Point>();

        for (int i = 0; i < 3; i++) {
            t1Rect.add(new Point2f(t1Points[i].x() - r1.x(), t1Points[i].y() - r1.y()));
            t2Rect.add(new Point2f(t2Points[i].x() - r2.x(), t2Points[i].y() - r2.y()));
            t2RectInt.add(new Point(t2Points[i].x() - r2.x(), t2Points[i].y() - r2.y()));
        }
        // mask 包含目标图片三个凸点的黑色矩形
        // MatExpr maskExpr = Mat.zeros(r2.height(), r2.width(), opencv_core.CV_32FC3);
        Mat mask = new Mat(r2.height(), r2.width(), CV_32FC3,new Scalar(0,0));
        opencv_imgproc.fillConvexPoly(mask,list2MP(t2RectInt), new Scalar(1.0, 1.0), 16, 0);

        Mat img1Rect = new Mat();
        FloatRawIndexer imgIndex = img1.createIndexer();
        //img1.submat(r1).copyTo(img1Rect);
        new Mat(img1,r1).copyTo(img1Rect);

        //img1.apply(r1).copyTo(img1Rect);
        // img2Rect 原始图片适应mask大小并调整位置的图片
        System.out.println(r2.height() + "======" + r2.width() + "=====" + img1Rect.type());
        MatExpr img2RectExpr = Mat.zeros(r2.height(), r2.width(), img1Rect.type());
        Mat img2Rect = img2RectExpr.asMat();
        img2Rect = applyAffineTransform(img2Rect, img1Rect, t1Rect, t2Rect);
        System.out.println(img2Rect.rows() + "======" + img2Rect.cols() + "=====" + img2Rect.type());
        opencv_core.multiply(img2Rect, mask, img2Rect); // img2Rect在mask三个点之间的图片
        Mat dst = new Mat();
        System.out.println(img2Rect.rows() + "======" + img2Rect.cols() + "=====" + img2Rect.type());

        MatExpr img2RectS = Mat.ones(r2.height(), r2.width(), CV_32FC3);
        Mat img2s = img2RectS.asMat();
        opencv_core.subtract(mask, img2s, dst);
        opencv_core.multiply(img2.apply(r2), dst, img2.apply(r2));
        opencv_core.absdiff(img2.apply(r2), img2Rect, img2.apply(r2));
        mask.release();
        return img2;
    }

    public  Mat applyAffineTransform(Mat warpImage, Mat src,List<Point2f> srcTri, List<Point2f> dstTri) {
        Mat warpMat = opencv_imgproc.getAffineTransform(list2MP2(srcTri),list2MP2(dstTri));
        opencv_imgproc.warpAffine(src, warpImage, warpMat, warpImage.size()); //, opencv_imgproc.INTER_LINEAR
        return warpImage;
    }

    /**
     * List exchange to MatOfPoint
     * @param points
     * @return
     */
    public  Mat list2MP(List<Point> points) {
        Mat points2 = new Mat(points.size());
        for(Point pf : points){
            Mat pm = new Mat(pf);
            points2.push_back(pm);
        }
        return points2;
    }

    public  Mat list2MP2d(List<Point2d> points) {
        Mat points2 = new Mat(points.size());
        for(Point2d pf : points){
            Mat pm = new Mat(pf);
            points2.push_back(pm);
        }
        return points2;
    }
    /**
     * List exchange to MatOfPoint2f
     * @param points
     * @return
     */
    public  Mat list2MP2(List<Point2f> points) {
        Mat points2 = new Mat(points.size());
        for(Point2f pf : points){
            Mat pm = new Mat(pf);
            points2.push_back(pm);
        }
        return points2;
    }



    //计算凸包办法
    public  Pair<List<Point>,List<Point2f>> getConvexHull(List<Point2f> points){
        // 计算凸包
        Mat convexPointsIdx2 = new Mat();
        //用了好久的时间实验出来的
        Mat points2m = new Mat(points.size());
        for(Point2f pf : points){
            Mat pm = new Mat(pf);
            points2m.push_back(pm);
        }
        //寻找凸包
        opencv_imgproc.convexHull(points2m, convexPointsIdx2,false,true);
        FloatRawIndexer matIndex = convexPointsIdx2.createIndexer();
        List<Point> convexPoints2 = new LinkedList<Point>();
        List<Point2f> convexPoints2f = new LinkedList<Point2f>();
        long rows = convexPointsIdx2.rows();
        for (int i = 0; i < rows; i++) {
            float x = matIndex.get(i,0);
            float y = matIndex.get(i,1);
            convexPoints2.add(new Point((int)x,(int)y));
            convexPoints2f.add(new Point2f(x,y));
        }
        return Pair.of(convexPoints2,convexPoints2f);

    }


    //计算凸包办法
    public  Pair<List<Point2f>,List<Point2f>> getConvexHull(Mat imgCV2, Mat imgCV1, List<Point2f> points1, List<Point2f> points2){
        // 计算凸包
        Mat imgCV1Warped = imgCV2.clone();
        imgCV1.convertTo(imgCV1, CV_32FC3);
        imgCV1Warped.convertTo(imgCV1Warped, CV_32FC3);

        Mat hull = new Mat();
        //用了好久的时间实验出来的
        Mat points2m = new Mat(points2.size());
        for(Point2f pf : points2){
            Mat pm = new Mat(pf);
            points2m.push_back(pm);
        }


        /***
         * void cv::convexHull (   InputArray  points,
         *                         OutputArray     hull,
         *                         bool    clockwise = false,
         *                         bool    returnPoints = true
         * )
         * ————————————————
         points:输入的二维点集，Mat类型数据即可
         hull:输出参数，用于输出函数调用后找到的凸包
         clockwise:操作方向，当标识符为真时，输出凸包为顺时针方向，否则为逆时针方向。
         returnPoints:操作标识符，默认值为true，此时返回各凸包的各个点，否则返回凸包各点的指数，当输出数组时std::vector时，此标识被忽略。
         */

        //得到凸包，hull是输出的带的凸包的参数
        opencv_imgproc.convexHull(points2m, hull,false,true);

        //hull2是调用opencv的convexHull方法得到对应的凸包的对应的位置
        //hull1是根据图像1的凸包的特征点的序号对应的得到的凸包位置
        List<Point2f> hull1 = new LinkedList<Point2f>();
        List<Point2f> hull2 = new LinkedList<Point2f>();
        // 保存组成凸包的关键点
        List<Point2f> hullPoinst = new LinkedList<Point2f>();
        FloatRawIndexer hullIndex = hull.createIndexer();
        long rows = hull.rows();
        for (int i = 0; i < rows; i++) {
            hullPoinst.add(new Point2f(hullIndex.get(i,0),hullIndex.get(i,1)));
        }
        if (hullPoinst.size() > 0) {
            for(Point2f hp : hullPoinst){
                for(int j=0, totalj=points1.size();j<totalj;j++){
                    if (hp.x() == points2.get(j).x() && hp.y() == points2.get(j).y()) {
                        hull1.add(points1.get(j));
                        hull2.add(points2.get(j));
                    }
                }
            }
        }


        //返回的就是两个图像的凸包的位置序列
        return  Pair.of(hull1, hull2);
    }

}
