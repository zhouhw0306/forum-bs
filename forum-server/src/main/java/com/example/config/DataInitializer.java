package com.example.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.domain.dao.Category;
import com.example.domain.dao.Tag;
import com.example.mapper.CategoryMapper;
import com.example.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 启动时按名称去重插入常见文章分类和标签
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    @Override
    public void run(String... args) {
        initCategories();
        initTags();
    }

    private void initCategories() {
        List<Category> categories = Arrays.asList(
            buildCategory("后端开发", "Java、Python、Go、Rust 等后端技术讨论"),
            buildCategory("前端开发", "Vue、React、Angular、CSS 等前端技术"),
            buildCategory("移动开发", "Android、iOS、Flutter、React Native 等"),
            buildCategory("AI / 机器学习", "深度学习、NLP、LLM、计算机视觉"),
            buildCategory("数据库", "MySQL、PostgreSQL、MongoDB、Redis 等"),
            buildCategory("DevOps", "Docker、K8s、CI/CD、Linux 运维"),
            buildCategory("数据结构与算法", "LeetCode、算法竞赛、设计模式"),
            buildCategory("开源项目", "开源工具推荐、项目实战、技术分享"),
            buildCategory("面试经验", "面经、刷题、职场成长"),
            buildCategory("技术问答", "答疑解惑、技术求助")
        );

        int added = 0;
        for (Category c : categories) {
            Integer exists = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, c.getCategoryName())
            );
            if (exists == null || exists == 0) {
                categoryMapper.insert(c);
                added++;
            }
        }
        log.info("文章分类：新增 {} 条，已有 {} 条", added, categories.size() - added);
    }

    private void initTags() {
        List<Tag> tags = Arrays.asList(
            buildTag("Java"),
            buildTag("Spring Boot"),
            buildTag("Python"),
            buildTag("Go"),
            buildTag("Rust"),
            buildTag("JavaScript"),
            buildTag("TypeScript"),
            buildTag("Vue"),
            buildTag("React"),
            buildTag("CSS"),
            buildTag("Node.js"),
            buildTag("MySQL"),
            buildTag("Redis"),
            buildTag("Docker"),
            buildTag("Kubernetes"),
            buildTag("Linux"),
            buildTag("Git"),
            buildTag("AI"),
            buildTag("LLM"),
            buildTag("算法"),
            buildTag("设计模式"),
            buildTag("HTTP"),
            buildTag("WebSocket"),
            buildTag("微服务"),
            buildTag("面试"),
            buildTag("工具推荐"),
            buildTag("开源"),
            buildTag("前端"),
            buildTag("后端"),
            buildTag("全栈")
        );

        int added = 0;
        for (Tag t : tags) {
            Integer exists = tagMapper.selectCount(
                new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, t.getTagName())
            );
            if (exists == null || exists == 0) {
                tagMapper.insert(t);
                added++;
            }
        }
        log.info("文章标签：新增 {} 条，已有 {} 条", added, tags.size() - added);
    }

    private Category buildCategory(String name, String desc) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setDescription(desc);
        return c;
    }

    private Tag buildTag(String name) {
        return Tag.builder().tagName(name).build();
    }
}
