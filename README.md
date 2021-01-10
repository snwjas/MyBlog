# MyBlog个人博客系统

后台预览链接：http://myblog.snwjas.xyz/madmin

## 介绍

MyBlog主要是基于 SpringBoot + Vue 前后端分离开发的一款动态个人博客系统，主要功能有文章管理、分类管理、标签管理、附件管理、评论管理、友链管理。学习 Java 也有一段时间了，这个是本人第一个真正意义上项目，希望借此项目锻炼自己编码能力，激励自己多写文章。

`鸣谢`：本项目在诸多方面，特别是后端UI设计借鉴学习了[Halo](https://halo.run/)。



## 技术使用

**开发环境**

- 工具：IDEA
- JDK 1.8
- 数据库：MySQL 8.0.15
- 项目构建：Maven 3.6

**后端**

- Web框架：SpringBoot
- 安全框架：SpringSecurity
- 字段校验：Hibernate-validator
- 持久层：MyBatis-Plus
- 接口文档：Swagger2
- Lombok 确保你的 IDE 安装了池插件
- Thumbnailator 图片压缩
- 使用基于内存的自定义缓存，可以更换成 Redis
- 等等

**前端**

- Vue.js 全家桶
- Element-UI 
- [vue-admin-template](https://github.com/PanJiaChen/vue-admin-template) 后台模板
- axios
- echarts
- mavon-editor
- 等等



## 快速开始

环境准备完毕后，修改配置文件`application-dev.yaml`中的`datasource`配置即可运行。

你也可以配置博客的一些属性，其配置前缀为`my-blog`

| 属性                               | 默认值                     | 说明                             |
| ---------------------------------- | -------------------------- | -------------------------------- |
| doc-enable                         | false                      | swagger api文档是否启用          |
| admin-path                         | “admin”                    | 后台管理入口，不需要添加'/'      |
| allow-login-failure-seconds        | 3600                       | 允许连续登录失败的时间（单位秒） |
| allow-login-failure-count          | 10                         | 允许登录失败的次数               |
| remember-me-token-validity-seconds | 604800                     | 登录记住我token时间(单位秒)      |
| file-save-path                     | “[user.home]/MyBlog/files” | 上传文件保存路径                 |



## 项目结构

```java
.|--src.main.java.xyz.snwjas.blog
    |---annotation //注解
    |---aspect //切入点aop
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
    |	|---impl //业务实现类
    |---support //其他的一些类
    |	|---cache //自定义缓存
    |---utils //工具包
```



## 数据库设计

数据库名：`myblogdb`，数据库统一编码：`utf8mb4`

**user : 用户表**

|    字段     |       类型       |   约束   |     说明     |
| :---------: | :--------------: | :------: | :----------: |
|     id      | int(11) unsigned |   主键   |    用户id    |
|  username   |   varchar(63)    | not null |  登录用户名  |
|  password   |   varchar(255)   | not null |   登录密码   |
|  nickname   |   varchar(127)   |          |   用户昵称   |
|    email    |   varchar(127)   |          |   用户邮箱   |
|   avatar    |  varchar(1023)   |          | 用户头像链接 |
| description |  varchar(1023)   |          |   个人描述   |
| creare_time |    timestamp     |          |   创建时间   |
| update_time |    timestamp     |          |   更新时间   |

**blog : 博客文章表**

可以将 original_content 和 format_content 抽离出一张表。

|       字段       |        类型         |   约束   |                 说明                 |
| :--------------: | :-----------------: | :------: | :----------------------------------: |
|        id        |  int(11) unsigned   |   主键   |                博客id                |
|      title       |    varchar(255)     | not null |               博客标题               |
| original_content |      longtext       |          |            原格式博客内容            |
|  format_content  |      longtext       |          |         格式化(html)博客内容         |
|       url        |    varchar(255)     |          |               博客链接               |
|     summary      |    varchar(511)     |          |               博客摘要               |
|    thumbnail     |    varchar(1023)    |          |            博客缩略图链接            |
|     top_rank     |       int(11)       |          |             博客置顶排行             |
|  allow_comment   | tinyint(4) unsigned |   0/1    | 0：不允许评论<br>1：允许评论（默认） |
|      likes       |  int(11) unsigned   |          |              喜欢的人数              |
|     dislikes     |  int(11) unsigned   |          |             不喜欢的人数             |
|      visits      |  int(11) unsigned   |          |               访问人数               |
|      status      | tinyint(4) unsigned |          |               博客状态               |
|   create_time    |      timestamp      |          |               创建时间               |
|   update_time    |      timestamp      |          |               更新时间               |

**category : 博客分类表**

|    字段     |       类型       |   约束   |        说明         |
| :---------: | :--------------: | :------: | :-----------------: |
|     id      | int(11) unsigned |   主键   |       分类id        |
|    name     |   varchar(63)    | not null |       分类名        |
|  parent_id  | int(11) unsigned |          | 父分类id，0顶层分类 |
| description |   varchar(127    |          |      分类描述       |
| creare_time |    timestamp     |          |      创建时间       |
| update_time |    timestamp     |          |      更新时间       |

**tag : 博客标签表**

|    字段     |       类型       |   约束   |   说明   |
| :---------: | :--------------: | :------: | :------: |
|     id      | int(11) unsigned |   主键   |  标签id  |
|    name     |   varchar(63)    | not null |  标签名  |
| creare_time |    timestamp     |          | 创建时间 |
| update_time |    timestamp     |          | 更新时间 |

 **blog_tag : 博客标签关联表**

|    字段     |       类型       |   约束   |    说明    |
| :---------: | :--------------: | :------: | :--------: |
|     id      | int(11) unsigned |   主键   | 博客标签id |
|   tag_id    | int(11) unsigned | not null |   标签id   |
|   blog_id   | int(11) unsigned | not null |   博客id   |
| creare_time |    timestamp     |          |  创建时间  |
| update_time |    timestamp     |          |  更新时间  |

**comment : 博客评论表**

|    字段     |        类型         |   约束   |            说明            |
| :---------: | :-----------------: | :------: | :------------------------: |
|     id      |  int(11) unsigned   |   主键   |           评论id           |
|   content   |    varchar(1023)    | not null |          评论内容          |
|   author    |     varchar(64)     | not null |          评论作者          |
|    email    |    varchar(127)     | not null |        评论作者邮箱        |
|  parent_id  |  int(11) unsigned   |          |          父评论id          |
| ip_address  |    varchar(127)     |          |      评论作者的ip地址      |
| user_agent  |    varchar(511)     |          |     评论作者的用户代理     |
|   blog_id   |  int(11) unsigned   | not null |           博客id           |
|   status    | tinyint(4) unsigned |          |          评论状态          |
|  is_admin   | tinyint(4) unsigned |          | 0: 访客（默认）；1：管理员 |
| create_time |      timestamp      |          |          创建时间          |
| update_time |      timestamp      |          |          更新时间          |

**attachment  : 附件表**

|    字段     |       类型       |   约束   |             说明             |
| :---------: | :--------------: | :------: | :--------------------------: |
|     id      | int(11) unsigned |   主键   |            文件id            |
|    name     |   varchar(255)   | not null |      文件名（包括后缀）      |
|    size     |    bigint(20)    | not null |       文件大小（字节）       |
|    path     |  varchar(1023)   | not null |           文件路径           |
| media_type  |   varchar(127)   | not null |         HTML资源类型         |
| thumb_path  |  varchar(1023)   |          |        文件缩略图路径        |
|    width    | int(11) unsigned |          | 文件为图片时，图片的宽度像素 |
|   height    | int(11) unsigned |          | 文件为图片时，图片的高度像素 |
| creare_time |    timestamp     |          |           创建时间           |
| update_time |    timestamp     |          |           更新时间           |

**link : 友链表**

|    字段     |       类型       |   约束   |   说明   |
| :---------: | :--------------: | :------: | :------: |
|     id      | int(11) unsigned |   主键   |  友链id  |
|    name     |   varchar(127)   | not null | 友链名称 |
|     url     |   varchar(255)   | not null | 友链链接 |
|    logo     |  varchar(1023)   |          | 友链logo |
|    rank     |     int(11)      |          | 友链排行 |
| description |   varchar(255)   |          | 友链描述 |
| creare_time |    timestamp     |          | 创建时间 |
| update_time |    timestamp     |          | 更新时间 |

options : 系统设置

|     字段     |       类型       |   约束   |    说明    |
| :----------: | :--------------: | :------: | :--------: |
|      id      | int(11) unsigned |   主键   | 系统设置id |
|  option_key  |   varchar(127)   | not null |     键     |
| option_value |  varchar(1023)   | not null |     值     |
| creare_time  |    timestamp     |          |  创建时间  |
| update_time  |    timestamp     |          |  更新时间  |