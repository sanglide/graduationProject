package com.example.cinema.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.sql.Blob;

@Getter
@Setter
public class musicContent {
//    private Blob blobRe;
    private File blobRe;
    private String test1;

//    public Blob getBlobRe() {
//        return blobRe;
//    }
    public File getBlobRe() {
        return blobRe;
    }
    public String getTest1(){
        return test1;
    }
}
