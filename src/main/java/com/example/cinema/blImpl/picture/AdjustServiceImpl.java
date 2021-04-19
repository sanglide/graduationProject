package com.example.cinema.blImpl.picture;

import com.example.cinema.bl.picture.AdjustService;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdjustServiceImpl implements AdjustService {
    @Autowired
    EyeAndMouthAdjust eyeAndMouthAdjust;

    @Autowired
    OpenCVFaceSwap openCVFaceSwap;

    UploadPhotoImpl uploadPhoto=new UploadPhotoImpl();

    @Override
    public ResponseVO adjustEye(String path, String savePath, String type){
        boolean isTrue=eyeAndMouthAdjust.findPoints(path, savePath, type);

        if(isTrue==false){
            return ResponseVO.buildFailure("false");
        }
        String result=uploadPhoto.uploadFromService("eye_adjust.jpg");
        return ResponseVO.buildSuccess(result);
    }

    @Override
    public ResponseVO adjustMouth(String path, String savePath, String type){
        boolean isTrue=eyeAndMouthAdjust.findPoints(path,savePath,type);

        if(isTrue==false){
            return ResponseVO.buildFailure("false");
        }
        String result=uploadPhoto.uploadFromService("mouth_adjust.jpg");
        return ResponseVO.buildSuccess(result);
    }
}
