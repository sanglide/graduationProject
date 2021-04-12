package com.example.cinema.bl.picture;

import com.example.cinema.vo.ResponseVO;

public interface AdjustService {

    /**
     * 这里对生成的照片进行眼睛放大
     * @return
     */
    ResponseVO adjustEye(String path, String savePath, String type);

    /**
     * 这里对生成的照片进行嘴巴的缩小
     * @return
     */
    ResponseVO adjustMouth(String path, String savePath, String type);
}
