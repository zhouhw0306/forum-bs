package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zhw
 */
@SpringBootApplication
@MapperScan("com/example/mapper")
public class ForumServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ForumServerApplication.class, args);
//        String[] names = run.getBeanDefinitionNames();
//        for (String name : names) {
//            System.out.println(name);
//        }
        //解决bug1
        //解决bug2
        //解决bug3
        //解决bug5
        // 解决4
        // 解决6
    }

}
