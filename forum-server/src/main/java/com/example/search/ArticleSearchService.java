package com.example.search;

import com.example.domain.dao.Article;
import com.example.service.ArticleService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhw
 */
@Service
public class ArticleSearchService {

    @Value("${spring.data.elasticsearch.repositories.enabled:false}")
    private String enable;
    @Resource
    private ArticleService articleService;
    @Autowired(required = false)
    private ArticleSearchRepository repository;
    @Resource
    private ElasticsearchRestTemplate restTemplate;

    public List<Article> searchByKey(String key){
        if (!Boolean.parseBoolean(enable)){
            List<Article> list = articleService.query().select("id", "title", "content_html", "comment_count", "view_count", "create_time").like("title", key).list();
            list.forEach(v -> {
                String title = v.getTitle();
                String replace =  title.replace(key, "<em>" + key + "</em>");
                v.setTitle(replace);
            });
            return list;
        }
        //创建构建器
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
        shouldQuery.should(QueryBuilders.matchQuery("contentHtml", key));
        shouldQuery.should(QueryBuilders.matchQuery("title", key));
        shouldQuery.minimumShouldMatch(1); //至少满足一个
        boolQuery.must(shouldQuery);
        builder.withQuery(boolQuery);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("contentHtml").field("title").preTags("<em>").postTags("</em>");
        builder.withHighlightBuilder(highlightBuilder);
        //排序
        builder.withSorts(SortBuilders.fieldSort("viewCount").order(SortOrder.DESC));
        //查询
        SearchHits<ArticleDoc> result = restTemplate.search(builder.build(), ArticleDoc.class);

        Iterator<SearchHit<ArticleDoc>> iterator = result.stream().iterator();
        List<SearchHit<ArticleDoc>> articleDocList = new ArrayList<>();
        iterator.forEachRemaining(articleDocList::add);
        List<Article> articles = new ArrayList<>();
        for (SearchHit<ArticleDoc> articleDocSearchHit : articleDocList) {
            ArticleDoc content = articleDocSearchHit.getContent();
            List<String> contents = articleDocSearchHit.getHighlightField("contentHtml");
            List<String> titles = articleDocSearchHit.getHighlightField("title");
            String contenthtml = StringUtils.join(contents, "... ");
            String title = StringUtils.join(titles, "... ");
            if (!StringUtils.isEmpty(contenthtml)){
                content.setContentHtml(contenthtml);
            }
            if (!StringUtils.isEmpty(title)){
                content.setTitle(title);
            }
            Article article = new Article();
            BeanUtils.copyProperties(content,article);
            articles.add(article);
        }
        return articles;
    }

    /**
     * 启动中es与mysql数据不一致时执行
     */
//    @PostConstruct
//    public void init(){
//        repository.deleteAll();
//        List<Article> list = articleService.list();
//        List<ArticleDoc> listDoc = new ArrayList<>();
//        for (Article article : list) {
//            ArticleDoc articleDoc = new ArticleDoc();
//            BeanUtils.copyProperties(article,articleDoc);
//            listDoc.add(articleDoc);
//        }
//        repository.saveAll(listDoc);
//    }

}
