package com.example.search;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * mysql 同步 es
 * 建议使用canal，不推荐这样手动同步
 * @ConditionalOnProperty 使用了es组件才启用该切面
 * @author zhw
 */
@Aspect
@Slf4j
@Component
@ConditionalOnProperty("spring.data.elasticsearch.repositories.enabled")
public class EsSyncMysqlAspect {

    @Autowired(required = false)
    ArticleSearchRepository repository;

    @Resource
    ArticleMapper articleMapper;

    /**
     * 更新时同步
     */
    @AfterReturning(value = "execution(* com.example.service.impl.ArticleServiceImpl.publishArticle(..))",returning = "result")
    public void saveOrUpdateArticle(Object result){
        JSONObject jsonObject = JSONUtil.parseObj(result);
        Integer code = jsonObject.getInt("code");
        if (code == 0){
            String articleId = jsonObject.getStr("data");
            ArticleDoc articleDoc = new ArticleDoc();
            BeanUtils.copyProperties(articleMapper.selectById(articleId),articleDoc);
            repository.save(articleDoc);
        }
    }

    /**
     * 删除时同步
     */
    @Around("execution(* com.example.controller.article.ArticleController.deleteById(..))")
    public Object deleteById(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        Integer code = jsonObject.getInt("code");
        if (code == 0){
            repository.deleteById((String)args[0]);
        }
        return result;
    }

}
