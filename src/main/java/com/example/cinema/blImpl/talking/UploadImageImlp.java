package com.example.cinema.blImpl.talking;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

/**
 * 2021.3.22
 * 这个函数的功能就是将项目根目录下的名为fileName的文件传到七牛云上去，对应的名字就是fileName*/
public class UploadImageImlp {
    public void uploadFromService(String fileName){
        String qianzhui="./image/";
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "r_0N4a-3NHcE4NjMH1yMk-xFdQoIk1JfNAihqipI";
        String secretKey = "-o6NR-HpbuGCdu-GTSpQsZQHMYcFFupzxHaMos4T";
        String bucket = "graduation00image";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = qianzhui+fileName;
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }


    }
//    public static void main(String[] args){
//        uploadFromService("result.png");
//    }
}
