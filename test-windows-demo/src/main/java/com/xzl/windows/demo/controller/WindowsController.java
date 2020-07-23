package com.xzl.windows.demo.controller;
import com.xzl.windows.WindowsClientDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desc:
 * @author: XZL
 * @createTime: 2020/7/23 15:59
 * @version: v0.0.1
 * @history:
 */
@RestController
@RequestMapping("/windows")
public class WindowsController {

    @Autowired
    private WindowsClientDemo clientDemo;

    @GetMapping("test1")
    public String test(String username,String password,String ip){
        //初始化sdk
        clientDemo.CameraInit();
        //注册
        clientDemo.register(username,password,ip,(short)8000);
        //布防
        clientDemo.SetupAlarmChan(ip);
        return "布防成功";
    }
}
