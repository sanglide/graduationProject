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

    @Override
    public ResponseVO adjustEye(String path, String savePath, String type){
        eyeAndMouthAdjust.findPoints(path, savePath, type);
        //String result_path=savePath+"result_adjust.jpg";
        //openCVFaceSwap.uploadPictureToAdvice(result_path,rp);
        return ResponseVO.buildSuccess("eye_adjust.jpg");
    }

    @Override
    public ResponseVO adjustMouth(String path, String savePath, String type){
        eyeAndMouthAdjust.findPoints(path,savePath,type);
        //String result_path=savePath+"result_adjust.jpg";
        //openCVFaceSwap.uploadPictureToAdvice(result_path,rp);
        return ResponseVO.buildSuccess("mouth_adjust.jpg");
    }
}
