package com.controller.test;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/boot")
@Slf4j
public class BootTest {

    @GetMapping("/hello")
    public String helloTest(){
        System.out.println("访问到了");
        log.info("6666666666666666666666666666666666666666666666666666");
        return "index";
    }
}
