package com.example.cinema.blImpl.picture;

import com.example.cinema.bl.picture.PhotoService;
import com.example.cinema.util.*;
import com.example.cinema.vo.ResponseVO;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Override
    public ResponseVO takePicture(HttpServletResponse rp){
        try{
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("Welcome to OpenCV " + Core.VERSION);

            // 新建窗口
            JFrame cameraFrame = new JFrame("camera");
            cameraFrame.setAlwaysOnTop(true);
            cameraFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            cameraFrame.setSize(640, 480);
            cameraFrame.setBounds(0, 0, cameraFrame.getWidth(), cameraFrame.getHeight());
            cameraFrame.setLocationRelativeTo(null);
            VideoPanel videoPanel = new VideoPanel();
            cameraFrame.setContentPane(videoPanel);
            cameraFrame.setVisible(true);

            CascadeClassifier faceCascade = new CascadeClassifier();
//            String property_1 = System.getProperty("user.dir");
//            String path_1 = property_1 + "\\src\\main\\resources\\lib\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_alt.xml";

            // 获取模型文件
            faceCascade.load("D:\\openCV\\opencv341\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_alt.xml");
            // 调用摄像头
            VideoCapture capture = new VideoCapture();
//            try {
//                capture.open(0);
//                if (capture.isOpened()) {
//                    Mat image = new Mat();
//                    int i=0;
//                    while(i<100) {
//                        capture.read(image);
//                        if (!image.empty()) {
//                            //detectAndDisplay(image, faceCascade);
//                            videoPanel.setImageWithMat(image);
//                            cameraFrame.repaint();
//                            i++;
////                        Thread.sleep(1000);
//                        } else {
//                            System.exit(0);//退出
//                            break;
//                        }
//                    }
//
//                    capture.read(image);
//                    videoPanel.saveImageWithMat(image);
//                    cameraFrame.repaint();
//                    //System.exit(2);//退出
//                }
//                Thread.sleep(200);//2000毫秒刷新一次图像
//                System.exit(0);//退出
//            }
//            catch (Exception e){}
//            finally {
//                capture.release();
//            }
            try {
                capture.open(0);
                if (capture.isOpened()) {
                    Mat image = new Mat();
                    int i=0;
                    while(i<100) {
                        capture.read(image);
                        i++;
                        if (!image.empty()) {
                            detectAndDisplay(image, faceCascade);
                            videoPanel.setImageWithMat(image);
                            cameraFrame.repaint();
                            Thread.sleep(10);

                        } else {
                            break;
                        }
                    }
                    System.out.println("wq");
                    capture.read(image);
                    videoPanel.saveImageWithMat(image);
                    capture.release();
                    cameraFrame.dispose();
                    System.out.println("wq1");
                    //System.exit(0);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                capture.release();
            }
            System.out.println("12");

            String property = System.getProperty("user.dir");
            String path = property + "\\src\\main\\resources\\static\\photos\\photo.jpg";
            File imageFile = new File(path);
            if (imageFile.exists()) {
                FileInputStream fis = null;
                OutputStream os = null;
                try {
                    fis = new FileInputStream(imageFile);
                    os = rp.getOutputStream();
                    int count = 0;
                    byte[] buffer = new byte[1024 * 8];
                    while ((count = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, count);
                       // os.flush();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            System.out.println(rp);


            return ResponseVO.buildSuccess();


        }catch (Exception e){
            System.out.println(e.toString());
            return ResponseVO.buildFailure("后端调用摄像头出错");
        }
    }



//    @Override
//    public ResponseVO takePictures() {
//        Webcam webcam = Webcam.getDefault();
//        webcam.setViewSize(WebcamResolution.VGA.getSize());
//        WebcamPanel panel = new WebcamPanel(webcam);
//        panel.setFPSDisplayed(true);
//        panel.setDisplayDebugInfo(true);
//        panel.setImageSizeDisplayed(true);
//        panel.setMirrored(true);
//
//        JFrame window = new JFrame("Test webcam panel");
//        window.add(panel);
//        window.setResizable(true);
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.pack();
//        window.setVisible(true);
//
//        final JButton button = new JButton("拍照");
//        window.add(panel, BorderLayout.CENTER);
//        window.add(button, BorderLayout.SOUTH);
//        window.setResizable(true);
//        window.pack();
//        window.setVisible(true);
//        try {
//            String fileName = "D://" + System.currentTimeMillis(); // 保存路径即图片名称（不用加后缀）
//            WebcamUtils.capture(webcam, fileName, ImageUtils.FORMAT_PNG);
//            capture.open(0);
//            if (capture.isOpened()) {
//                Mat image = new Mat();
//                int i=0;
//                while(i<100) {
//                    capture.read(image);
//                    if (!image.empty()) {
//                        //detectAndDisplay(image, faceCascade);
//                        videoPanel.setImageWithMat(image);
//                        cameraFrame.repaint();
//                        i++;
////                        Thread.sleep(1000);
//                    } else {
//                        System.exit(0);//退出
//                        break;
//                    }
//                }
//
//                capture.read(image);
//                videoPanel.saveImageWithMat(image);
//                cameraFrame.repaint();
//                //System.exit(2);//退出
//            }
//            Thread.sleep(200);//2000毫秒刷新一次图像
//            System.exit(0);//退出
//        }
//        catch (Exception e){}
//        finally {
//            capture.release();
//        }
//        System.out.println("12");
//        button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//
//                button.setEnabled(false); // 设置按钮不可点击
//
//                // 实现拍照保存-------start
//                String fileName = "D://" + System.currentTimeMillis(); // 保存路径即图片名称（不用加后缀）
//                WebcamUtils.capture(webcam, fileName, ImageUtils.FORMAT_PNG);
//                SwingUtilities.invokeLater(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        JOptionPane.showMessageDialog(null, "拍照成功");
//                        button.setEnabled(true); // 设置按钮可点击
//
//                        return;
//                    }
//                });
//                // 实现拍照保存-------end
//
//            }
//        });
//
//        return ResponseVO.buildSuccess();
//    }



    /**
     * 绘制图形界面
     * @param frame
     * @param faceCascade
     */
    public static void detectAndDisplay(Mat frame, CascadeClassifier faceCascade)
    {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        int absoluteFaceSize = 0;
        int height = grayFrame.rows();
        if (Math.round(height * 0.2f) > 0) {
            absoluteFaceSize = Math.round(height * 0.2f);
        }

        // detect faces
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
    }



    /**
     *
     * @param f 表示帧
     * @param targetFileName 存储路径
     */
    public static void doExecuteFrame(Frame f, String targetFileName) {
        if (null ==f ||null ==f.image) {
            return;
        }
        Java2DFrameConverter converter =new Java2DFrameConverter();
        BufferedImage bi =converter.getBufferedImage(f);
        File output =new File(targetFileName);
        try {
            ImageIO.write(bi,"jpg",output);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
