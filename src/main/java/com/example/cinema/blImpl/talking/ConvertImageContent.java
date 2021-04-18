package com.example.cinema.blImpl.talking;

import com.example.cinema.po.ImageContent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ConvertImageContent {
    String background;
    public ConvertImageContent(String background){
        this.background=background;
    }
    public static void main(String[] args) throws IOException {
        /**这个函数就是输入一个字符串，生成一张图片*/
        List<String> info1=new ArrayList<String>(Arrays.asList("小女孩","外婆","帽子"));
        List<String> info2=new ArrayList<String>(Arrays.asList("妈妈", "小女孩","酒","蛋糕"));
        List<String> info3=new ArrayList<String>(Arrays.asList("小女孩","狼"));
        List<String> info4=new ArrayList<String>(Arrays.asList("外婆", "狼"));
        List<String> info5=new ArrayList<String>(Arrays.asList("扮成外婆的狼", "小女孩"));
        List<String> info6=new ArrayList<String>(Arrays.asList("扮成外婆的狼", "猎人"));
        List<String> info7=new ArrayList<String>(Arrays.asList("猎人", "小女孩","外婆","剪开肚子的狼"));
        List<String> info8=new ArrayList<String>(Arrays.asList("猎人", "小女孩","外婆","蛋糕","酒"));

        ConvertImageContent convertImageContent=new ConvertImageContent("tree");
        convertImageContent.convertImage(info7);

    }
    public String convertImage(List<String> info) throws IOException {
        /**这个函数把字符串list转成上传到服务器上的图片哈希值*/
        //先存一个名为result11的背景图
        String background_path="./image/background-"+background+".png";
        BufferedImage buffImg = ImageIO.read(new File(background_path));
        generateWaterFile(buffImg, "./image/result11.png");


        for(int i=0;i<info.size();i++){
            String temp=info.get(i);
            coverImage(temp);
        }
        //本地存储的图片名为result11.png，传到服务气上并返回名字
        UploadImageImlp uploadImageImlp=new UploadImageImlp();
        String result=uploadImageImlp.uploadFromService("result11.png");
        System.out.println("此次生成的图片的名字："+result);
        return result;
//        return "";
    }
    public void coverImage(String temp) throws IOException {
        //下载temp所在素材；
        String rootPath="./image";
        FileOpera fileOpera=new FileOpera();
        List<String> folderNameList= (List<String>) (fileOpera.getFilesName(rootPath,null)).get("folderNameList");
        System.out.println(folderNameList);
        // 获取temp所属类型；
        String imageType="";
        for(int i=0;i<folderNameList.size();i++){
            String tempp=folderNameList.get(i);
            String path=rootPath+"/"+tempp+"/"+temp+".png";
            HashMap<String,Object> filesName = fileOpera.getFilesName(path, null);
            System.out.println(filesName);
            if(filesName.get("retType").equals("2")){
                imageType=tempp;
                break;
            }
        }
        System.out.println(temp+"的类型是："+imageType);
        List<Integer> position=getPosition(imageType,temp);
        int x=position.get(0);
        int y=position.get(1);
        // 将temp叠到result11上
        String finiPath=rootPath+"/"+imageType+"/"+temp+".png";
        System.out.println("开始叠图了,x="+x+",y="+y+",finiPath="+finiPath);
        BufferedImage buffImg = watermark(new File("./image/result11.png"),
                new File(finiPath),
                x, y, 1.0f);
        // 输出水印图片
        generateWaterFile(buffImg, "./image/result11.png");
    }
    private List<Integer> getPosition(String type,String temp){
        //参数是这个图片元素的类型，返回一个二元list，存储应该返回的x、y坐标
        List<Integer> re=new ArrayList<Integer>();
        if(type.equals("people")){
            if(temp.equals("小红帽")|temp.equals("小女孩")){
                re.add(new Integer(370));
                re.add(new Integer(150));
            }else if(temp.equals("外婆")|temp.equals("妈妈")|temp.equals("扮成外婆的狼")){
                re.add(new Integer(130));
                re.add(new Integer(125));
            }else if(temp.equals("狼")){
                re.add(new Integer(250));
                re.add(new Integer(150));
            }else if(temp.equals("猎人")){
                re.add(new Integer(250));
                re.add(new Integer(150));
            }else if(temp.equals("剪开肚子的狼")){
                re.add(new Integer(0));
                re.add(new Integer(280));
            }
        }else if(type.equals("hand")){
            if(temp.equals("蛋糕")){
                re.add(new Integer(470));
                re.add(new Integer(290));
            }else if(temp.equals("帽子")){
                re.add(new Integer(320));
                re.add(new Integer(225));
            }else if(temp.equals("酒")){
                re.add(new Integer(400));
                re.add(new Integer(235));
            }

        }


        return re;
    }
    private void generateWaterFile(BufferedImage buffImg, String savePath) {
        int temp = savePath.lastIndexOf(".") + 1;
        try {
            ImageIO.write(buffImg, savePath.substring(temp), new File(savePath));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public static BufferedImage watermark(File file, File waterFile, int x, int y, float alpha) throws IOException {
        // 获取底图
        BufferedImage buffImg = ImageIO.read(file);
        // 获取层图
        BufferedImage waterImg = ImageIO.read(waterFile);
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 绘制
        g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
        g2d.dispose();// 释放图形上下文使用的系统资源
        return buffImg;
    }



}
