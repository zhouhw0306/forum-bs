package com.example.event.publisher;

import com.example.domain.dto.ArticleViewDTO;
import com.example.event.event.ArticleViewAddEvent;
import com.example.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author zhw
 */
@Component
@Slf4j
public class ForEventPublisher {

    @Autowired
    @Lazy
    private ApplicationEventPublisher publisher;

    public void articleViewPublish(String articleId){
        ArticleViewDTO articleViewDTO = new ArticleViewDTO(UserUtils.getCurrentUser(),articleId);
        try {
            publisher.publishEvent(new ArticleViewAddEvent(this, articleViewDTO));
        }catch (Exception e){
            log.error("浏览量增加失败");
        }
    }
}
