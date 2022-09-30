package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author zhw
 */
@SpringBootApplication
@MapperScan("com/example/mapper")
public class ForumServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumServerApplication.class, args);
    }

}
