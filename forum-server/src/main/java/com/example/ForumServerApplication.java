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
        System.out.println("swagger:"+"http://localhost:8888/swagger-ui.html");
        System.out.println("knife4j:"+"http://localhost:8880/doc.html");
        // 01
    }

}
