package com.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.IOException;

/**
 * 20191119
 * @auther lfp
 */
@Configuration
public class IndexConfig {
    @EventListener({ApplicationReadyEvent.class})
    void applicationReadyEvent() {
        String url = "http://localhost:80/login/index.do";
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (IOException e) {
            System.out.println("启动失败");
            e.printStackTrace();
        }
        System.out.println("启动成功");
    }
}