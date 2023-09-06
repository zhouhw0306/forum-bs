package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class ApplicationContextHelper implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextHelper.class);

    private static ApplicationContext appCtx;

    private static boolean isStart = false;


    /**
     * 这是一个便利的方法，帮助我们快速得到一个BEAN
     *
     * @param beanName bean的名字
     * @return 返回一个bean对象
     */
    public static Object getBean(String beanName) {
        return appCtx.getBean(beanName);
    }


    public static Object getBean(Class clazz) {
        return appCtx.getBean(clazz);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!isStart) {
            logger.info("ApplicationContextHelper初始化");
            isStart = true;
            appCtx = contextRefreshedEvent.getApplicationContext();
        }
    }
}

