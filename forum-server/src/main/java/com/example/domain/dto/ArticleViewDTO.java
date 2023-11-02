package com.example.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhw
 */
@Data
@AllArgsConstructor
public class ArticleViewDTO {

    private String userId;
    private String articleId;
}
