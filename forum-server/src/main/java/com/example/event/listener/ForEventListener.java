package com.example.event.listener;

import com.example.domain.dto.ArticleViewDTO;
import com.example.event.event.ArticleViewAddEvent;
import com.example.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zhw
 */
@Slf4j
@Component
public class ForEventListener {

    @Resource
    ArticleMapper articleMapper;

    /**
     * @TransactionalEventListener 可以在指定的事务阶段下才执行
     * 可选值fallbackExecution 不存在事务时是否执行
     */
//    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = false,value = ArticleViewAddEvent.class)
    public void onCmdTraceEvent(ArticleViewAddEvent event) {
        ArticleViewDTO articleViewDTO = Objects.requireNonNull(event.getArticleViewDTO(), "文章浏览数据为空");
        articleMapper.addViewCount(articleViewDTO.getArticleId());
    }
}
