package com.example.cinema.controller.router;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author deng
 * @date 2019/03/11
 */
@Controller
public class ViewController {
    @RequestMapping(value = "/index")
    public String getIndex() {
        return "index";
    }

    @RequestMapping(value = "/user/selectPs")
    public String getSelectFace() {
        return "selectPs";
    }

    @RequestMapping(value = "/user/selectWhichPs")
    public String getSelectPs() {
        return "selectWhichPs";
    }

    @RequestMapping(value = "/user/selectFix")
    public String getSelectFix() {
        return "selectFix";
    }

    @RequestMapping(value = "/user/selectWhichFix")
    public String getSelectWhichFix() {
        return "selectWhichFix";
    }

    @RequestMapping(value = "/user/selectOcean")
    public String getSelectOcean() {
        return "selectOcean";
    }

    @RequestMapping(value = "/user/selectHouse")
    public String getSelectHouse() {
        return "selectHouse";
    }

    @RequestMapping(value = "/user/selectTree")
    public String getSelectTree() {
        return "selectTree";
    }

    @RequestMapping(value = "/user/start")
    public String getSrart() {
        return "start";
    }

    @RequestMapping(value = "/user/startSelect")
    public String getSelect() {
        return "startSelect";
    }
    @RequestMapping(value = "/user/startTalking")
    public String getTalking() {
        return "startTalking";
    }

    @RequestMapping(value = "/signUp")
    public String getSignUp() {
        return "signUp";
    }


}
