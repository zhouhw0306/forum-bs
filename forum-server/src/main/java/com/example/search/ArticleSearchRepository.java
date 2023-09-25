package com.example.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author zhw
 */
public interface ArticleSearchRepository extends ElasticsearchRepository<ArticleDoc, String> {

}
