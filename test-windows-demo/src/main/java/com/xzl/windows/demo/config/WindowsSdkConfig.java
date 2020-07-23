package com.xzl.windows.demo.config;
import com.xzl.windows.WindowsClientDemo;
import com.xzl.windows.demo.service.FMSGCallBack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @desc:
 * @author: XZL
 * @createTime: 2020/7/23 13:50
 * @version: v0.0.1
 * @history:
 */
@Configuration
public class WindowsSdkConfig {

    @Bean
    public WindowsClientDemo clientDemo(){
        return new WindowsClientDemo(new FMSGCallBack());
    }
}
