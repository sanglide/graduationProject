package com.example.cinema.po;

import com.example.cinema.vo.TicketVO;
import com.example.cinema.vo.TicketWithScheduleVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Image {

    /**
     * 图片id
     */
    private int id;
    /**
     * 页面名字
     */
    private String page_name;
    /**
     * 哈希值
     */
    private String hash_name;

    public Image() {
    }


}
