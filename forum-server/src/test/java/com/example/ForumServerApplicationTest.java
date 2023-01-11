package com.example;

import com.example.annotation.Authentication;
import com.example.constant.AuthConstant;
import com.example.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ForumServerApplicationTest {

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Test
    public void sensitiveFilter(){
//        String text = "王者达到王者荣耀de赌博赌";
//        System.out.println(sensitiveFilter.filter(text));
    }

}
