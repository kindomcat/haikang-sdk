package com.xzl.demo.config;
import com.xzl.demo.service.FMSGCallBack;
import com.xzl.linux.ClientDemo;
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
public class SdkConfig {

    @Bean
    public ClientDemo clientDemo(){
        return new ClientDemo(new FMSGCallBack());
    }
}
