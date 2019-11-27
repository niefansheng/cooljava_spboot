package com.config;

import com.quantity.Global;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 图片绝对路径映射虚拟访问路径
 */
@Configuration
public class VirtualPathConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String realPath = "file:/";
        if(Global.getOS()){
        //Windows系统
            realPath += Global.WINFILEPATH;
        }else{
            //Linux系统
            realPath += Global.LINUXFILEPATH;
        }
        //文件磁盘图片url 映射
        //配置server虚拟路径，handler为前台访问的目录，locations为files相对应的本地路径
        registry.addResourceHandler("/photo/**").addResourceLocations(realPath);
    }

}
