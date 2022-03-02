# MyBlog个人博客系统

**传送门**

- 预览地址：http://myblog.refrain.xyz/madmin
- 后台管理：[Github](https://github.com/snwjas/MyBlog) | [Gitee](https://gitee.com/snwjas/my-blog)
- 后台前端：[Github](https://github.com/snwjas/MyBlog-Admin) | [Gitee](https://gitee.com/snwjas/my-blog-admin)
- 前台前端：[Github](https://github.com/snwjas/MyBlog-App) | [Gitee](https://gitee.com/snwjas/my-blog-app)

## 项目介绍

MyBlog主要是基于 SpringBoot + Vue 前后端分离开发的一款动态个人博客系统，主要功能有文章管理、分类管理、标签管理、附件管理、评论管理和友链管理等。学习 Java 也有一段时间了，这个是本人第一个真正意义上项目，希望借此项目锻炼自己编码能力，激励自己多写文章。

`鸣谢`：本项目在诸多方面，特别是UI设计方面，借鉴学习了[Halo](https://halo.run/)。

如果本项目对你有帮助，不妨点个***Star***，你的支持就是对我行动的最大鼓励！



## 技术使用

**规范实现**：

- 统一请求响应
- 统一异常处理
- Java Bean Validation参数校验
- 面向AOP编程：通过自定义注解实现接口限流、操作日志记录等
- ……

**开发环境**

- 工具：IntelliJ IDEA
- JDK 1.8
- 数据库：MySQL 8.0.15
- 项目构建：后端Maven、前端 Webpack

**后端**

- Web框架：Spring Boot
- 安全框架：Spring Security
- 字段校验：Spring Validation
- 持久层：[MyBatis-Plus](https://mybatis.plus/)
- 接口文档：Swagger2
- Lombok：请确保您的 IDE 安装了此插件
- 使用简单的自定义缓存，可更换成 Redis
- 其他：[Thumbnailator](https://github.com/coobird/thumbnailator)、[wordfilter](https://gitee.com/sharegpj/wordfilter)、等等

**前端**

- Vue.js2 全家桶
- Element-UI
- [vue-admin-template](https://github.com/PanJiaChen/vue-admin-template) 后台模板
- axios
- echarts
- [mavon-editor](https://github.com/hinesboy/mavonEditor)
- 等等



## 快速开始

环境准备完毕后，修改配置文件`application-dev.yaml`中的`datasource`配置即可以开发环境运行：

- 默认后台地址：http://localhost:9096/admin

- 默认账号密码：admin / 123456

你也可以配置博客的一些属性，其配置前缀为`my-blog`

| 属性                               | 默认值                     | 说明                           |
| ---------------------------------- | -------------------------- | ------------------------------ |
| doc-enable                         | false                      | swagger api文档是否启用        |
| admin-path                         | “admin”                    | 后台管理入口，不需要添加'/'    |
| admin-web-path                     | “classpath:/admin/”        | 管理端WEB静态文件所在目录      |
| app-web-path                       | “classpath:/app/”          | 展示前端WEB静态文件所在目录    |
| allow-login-failure-seconds        | 3600                       | 允许连续登录失败的时间(单位秒) |
| allow-login-failure-count          | 10                         | 允许连续登录失败的次数         |
| remember-me-token-validity-seconds | 604800                     | 登录记住我token时间(单位秒)    |
| file-save-path                     | “[user.home]/MyBlog/files” | 上传文件保存路径               |

请将打包后的管理端 / 展示前端文件分别放置以上属性`admin-web-path` / `app-web-path`目录

## 项目结构 

```java
.|--src.main.java
    |---xyz.snwjas.blog //源码
        |---annotation //注解
        |---aspect //aop切入点
        |---config //配置
        |---constant //常量
        |---controller //控制器
        |	|---admin //后台控制器
        |	|---app //前台控制器
        |---exception //自定义异常
        |---handler //处理器
        |---interceptor //拦截器
        |---mapper //MyBatis mapper接口
        |	|---xml //MyBatis mapper xml 文件
        |---model //模型
        |	|---base //基本接口/类
        |	|---entity //实体类
        |	|---enums //枚举常量
        |	|---params //查询参数
        |	|---vo //视图对象
        |---schedule //定时任务
        |---service //业务接口
        |	|---impl //业务接口实现类
        |---support //其他的一些支持类
        |	|---cache //自定义缓存
        |	|---security //Spring Security 认证逻辑
        |	|---wordfilter //敏感词过滤器
        |---utils //工具包
    |---resources //资源
        |---admin //后台web资源
        |---app //前台web资源
        |---static //静态资源
        |---wordfilter //敏感词文件
```



## 数据库设计

数据库名：`myblogdb`，数据库统一编码：`utf8mb4`

本项目共设计了 11个表，分别是：user(用户表)、blog(文章表)、category(分类表)、tag(标签表)、blog_tag(文章标签关联表)、comment(评论表)、attachment(附件表)、link(友链表)、options(系统选项/设置表)、log(日志表)和statistics(数据统计表)，具体设计如下：

**user : 用户表**

|    字段     |       类型       |   约束   |   说明   |
| :---------: | :--------------: | :------: | :------: |
|     id      | int(11) unsigned |   主键   |  用户id  |
|  username   |   varchar(63)    | not null |  用户名  |
|  password   |   varchar(255)   | not null |   密码   |
|  nickname   |   varchar(127)   |          |   昵称   |
|    email    |   varchar(127)   |          |   邮箱   |
|   avatar    |  varchar(1023)   |          | 头像链接 |
| description |  varchar(1023)   |          | 个人描述 |
| update_time |    timestamp     |          | 更新时间 |
| create_time |    timestamp     |          | 创建时间 |

**blog : 博客文章表**

可以将 original_content 和 format_content 抽离出一张表。

|       字段       |        类型         |   约束   |           说明           |
| :--------------: | :-----------------: | :------: | :----------------------: |
|        id        |  int(11) unsigned   |   主键   |          博客id          |
|      title       |    varchar(255)     | not null |         文章标题         |
| original_content |      longtext       |          | 原格式(markdown)文章内容 |
|  format_content  |      longtext       |          |   格式化(html)文章内容   |
|       url        |    varchar(255)     |          |       文章访问链接       |
|     summary      |    varchar(511)     |          |         文章摘要         |
|    thumbnail     |    varchar(1023)    |          |      文章缩略图链接      |
|     top_rank     |       int(11)       |          |       文章置顶排行       |
|  allow_comment   | tinyint(4) unsigned |          | 0: 不允许; 1: 允许(默认) |
|      likes       |  int(11) unsigned   |          |        点赞的人数        |
|      visits      |  int(11) unsigned   |          |         访问人数         |
|      status      | tinyint(4) unsigned |          |         文章状态         |
|   update_time    |      timestamp      |          |         更新时间         |
|   create_time    |      timestamp      |          |         创建时间         |

**category : 博客分类表**

|    字段     |       类型       |   约束   |        说明         |
| :---------: | :--------------: | :------: | :-----------------: |
|     id      | int(11) unsigned |   主键   |       分类id        |
|    name     |   varchar(63)    | not null |       分类名        |
|  parent_id  | int(11) unsigned |          | 父分类id，0顶层分类 |
| description |   varchar(127)   |          |      分类描述       |
| update_time |    timestamp     |          |      更新时间       |
| create_time |    timestamp     |          |      创建时间       |

**tag : 博客标签表**

|    字段     |       类型       |   约束   |   说明   |
| :---------: | :--------------: | :------: | :------: |
|     id      | int(11) unsigned |   主键   |  标签id  |
|    name     |   varchar(63)    | not null |  标签名  |
| update_time |    timestamp     |          |      更新时间       |
| create_time |    timestamp     |          |      创建时间       |

 **blog_tag : 博客标签关联表**

|    字段     |       类型       |   约束   |    说明    |
| :---------: | :--------------: | :------: | :--------: |
|     id      | int(11) unsigned |   主键   | 博客标签id |
|   tag_id    | int(11) unsigned | not null |   标签id   |
|   blog_id   | int(11) unsigned | not null |   博客id   |
| update_time |    timestamp     |          |   更新时间   |
| create_time |    timestamp     |          |   创建时间   |

**comment : 博客评论表**

|    字段     |        类型         |   约束   |            说明            |
| :---------: | :-----------------: | :------: | :------------------------: |
|     id      |  int(11) unsigned   |   主键   |           评论id           |
|  parent_id  |  int(11) unsigned   |          | 父评论id，0顶层评论，默认0 |
|   content   |    varchar(1023)    | not null |          评论内容          |
|   author    |     varchar(64)     | not null |          评论作者          |
|    email    |    varchar(127)     | not null |        评论作者邮箱        |
|   avatar    |    varchar(1023)    |          |        评论作者头像        |
| ip_address  |       int(11)       |          |  评论作者的IPv4地址，整型  |
| user_agent  |    varchar(511)     |          |     评论作者的用户代理     |
|   blog_id   |  int(11) unsigned   | not null |           博客id           |
|   status    | tinyint(4) unsigned |          |          评论状态          |
|  is_admin   | tinyint(4) unsigned |          |  0: 访客(默认); 1: 管理员  |
| update_time |      timestamp      |          |          更新时间          |
| create_time |      timestamp      |          |          创建时间          |

**attachment  : 附件表**

|    字段     |       类型       |   约束   |             说明             |
| :---------: | :--------------: | :------: | :--------------------------: |
|     id      | int(11) unsigned |   主键   |            文件id            |
|    name     |   varchar(255)   | not null |            文件名            |
|    size     | int(11) unsigned | not null |       文件大小（字节）       |
|    path     |  varchar(1023)   | not null |           文件路径           |
| media_type  |   varchar(127)   | not null |        互联网媒体类型        |
| thumb_path  |  varchar(1023)   |          |        文件缩略图路径        |
|    width    | int(11) unsigned |          | 文件为图片时，图片的宽度像素 |
|   height    | int(11) unsigned |          | 文件为图片时，图片的高度像素 |
| update_time |    timestamp     |          |           更新时间           |
| create_time |    timestamp     |          |           创建时间           |

**link : 友链表**

|    字段     |       类型       |   约束   |   说明   |
| :---------: | :--------------: | :------: | :------: |
|     id      | int(11) unsigned |   主键   |  友链id  |
|    name     |   varchar(127)   | not null | 友链名称 |
|     url     |   varchar(255)   | not null | 友链链接 |
|    logo     |  varchar(1023)   |          | 友链logo |
|  top_rank   |     int(11)      |          | 友链排行 |
| description |   varchar(255)   |          | 友链描述 |
| update_time |    timestamp     |          | 更新时间 |
| create_time |    timestamp     |          | 创建时间 |

**options : 系统设置**

|     字段     |       类型       |   约束   |    说明    |
| :----------: | :--------------: | :------: | :--------: |
|      id      | int(11) unsigned |   主键   | 系统设置id |
|  option_key  |   varchar(127)   | not null |     键     |
| option_value |  varchar(1023)   | not null |     值     |
| update_time  |    timestamp     |          |  更新时间  |
| create_time  |    timestamp     |          |  创建时间  |

**log : 日志表**

|    字段     |        类型         | 约束 |          说明          |
| :---------: | :-----------------: | :--: | :--------------------: |
|     id      |  int(11) unsigned   | 主键 |         日志id         |
|   content   |    varchar(1023)    |      |        操作内容        |
|    type     | tinyint(4) unsigned |      |        操作类型        |
| ip_address  |       int(11)       |      | 操作人的ipv4地址，整型 |
| update_time |      timestamp      |      |        更新时间        |
| create_time |      timestamp      |      |        创建时间        |

**statistics : 统计表（统计每日的数据）**

|       字段       |       类型       |   约束   |    说明    |
| :--------------: | :--------------: | :------: | :--------: |
|        id        | int(11) unsigned |   主键   |   统计id   |
| web_visit_count  | int(11) unsigned |          | 网站访问量 |
| blog_visit_count | int(11) unsigned |          | 文章访问量 |
|  comment_count   | int(11) unsigned |          |  评论数量  |
|       date       |    timestamp     | not null |  统计日期  |
|   update_time    |    timestamp     |          |  更新时间  |
|   create_time    |    timestamp     |          |  创建时间  |

**speciallist : 特殊清单**

|    字段     |        类型         | 约束 |   说明   |
| :---------: | :-----------------: | :--: | :------: |
|     id      |  int(11) unsigned   | 主键 |    ID    |
|    type     | tinyint(4) unsigned |      |   类型   |
|   content   |    varchar(511)     |      |   内容   |
| update_time |      timestamp      |      | 更新时间 |
| create_time |      timestamp      |      | 创建时间 |
