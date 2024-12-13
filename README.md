### codebase编程社区

技术：springboot+mybatisPlus+vue2+elementUi

运行环境：jdk1.8、Mysql、Redis、Maven、Node.js

| tool      | version |
| ----------- | ----------- |
| Node.js      | v18.19.0       |
| npm   | v10.2.3        |

下载代码
```
git clone https://github.com/zhouhw0306/forum-bs
```
前端启动
```
npm install
npm run serve
```
后端启动
```
mvn spring-boot:run
```
# 项目亮点

1. 使用基于用户的协同过滤推荐算法
2. 使用前缀树结构实现敏感词过滤
3. Aop+Redis+自定义注解,实现接口权限校验、限流、重复提交验证功能
4. 使用JWT生成token实现登录验证
5. 集成MarkDown插件实现图片上传、代码高亮等
6. 集成Github授权登录OAuth2.0方式
7. 使用七牛云oss对象存储
8. 集成讯飞星火ai大模型
9. 集成elasticsearch搜索引擎

# 页面展示
![首页](https://img-blog.csdnimg.cn/c7cccb6f7ccc4540b378e93eb268ccda.png)
![chatAi](https://img-blog.csdnimg.cn/3dbe0061da124a5095ecb64fa5973aa4.png)
![文章](https://img-blog.csdnimg.cn/81bd374023904c19b705908c04a432a9.png)
![后台管理](https://img-blog.csdnimg.cn/a3d03193a79c452e82165aa6dc7bd490.png)