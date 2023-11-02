package com.example.event.event;

import com.example.domain.dto.ArticleViewDTO;
import org.springframework.context.ApplicationEvent;

/**
 * @author zhw
 */
public class ArticleViewAddEvent extends ApplicationEvent {


    private static final long serialVersionUID = -2670636857537086191L;
    private ArticleViewDTO articleViewDTO;

    public ArticleViewAddEvent(Object source, ArticleViewDTO articleViewDTO) {
        super(source);
        this.articleViewDTO = articleViewDTO;
    }

    public ArticleViewDTO getArticleViewDTO() {
        return articleViewDTO;
    }

    public void setArticleViewDTO(ArticleViewDTO articleViewDTO) {
        this.articleViewDTO = articleViewDTO;
    }
}
