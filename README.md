# SpringBoot 项目初始模板

> 作者：[程序员鱼皮](https://github.com/liyupi)
> 仅分享于 [编程导航知识星球](https://yupi.icu)

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

只需 1 分钟即可完成内容网站的后端！！！大家还可以在此基础上快速开发自己的项目。

[toc]

## 模板特点

### 主流框架 & 特性

- Spring Boot 2.7.x（贼新）
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring Boot 调试工具和项目处理器
- Spring AOP 切面编程
- Spring Scheduler 定时任务
- Spring 事务注解

### 数据存储

- MySQL 数据库
- Redis 内存数据库
- Elasticsearch 搜索引擎
- 腾讯云 COS 对象存储

### 工具类

- Easy Excel 表格处理
- Hutool 工具库
- Gson 解析库
- Apache Commons Lang3 工具类
- Lombok 注解

### 业务特性

- Spring Session Redis 分布式登录
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- Swagger + Knife4j 接口文档
- 自定义权限注解 + 全局校验
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置


## 业务功能

- 提供示例 SQL（用户、帖子、帖子点赞、帖子收藏表）
- 用户登录、注册、注销、更新、检索、权限管理
- 帖子创建、删除、编辑、更新、数据库检索、ES 灵活检索
- 帖子点赞、取消点赞
- 帖子收藏、取消收藏、检索已收藏帖子
- 帖子全量同步 ES、增量同步 ES 定时任务
- 支持微信开放平台登录
- 支持微信公众号订阅、收发消息、设置菜单
- 支持分业务的文件上传

### 单元测试

- JUnit5 单元测试
- 示例单元测试类

### 架构设计

- 合理分层


## 快速上手

> 所有需要修改的地方鱼皮都标记了 `todo`，便于大家找到修改的位置~

### MySQL 数据库

1）修改 `application.yml` 的数据库配置为你自己的：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

2）执行 `sql/create_table.sql` 中的数据库语句，自动创建库表

3）启动项目，访问 `http://localhost:8101/api/doc.html` 即可打开接口文档，不需要写前端就能在线调试接口了~

![](doc/swagger.png)

### Redis 分布式登录

1）修改 `application.yml` 的 Redis 配置为你自己的：

```yml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456
```

2）修改 `application.yml` 中的 session 存储方式：

```yml
spring:
  session:
    store-type: redis
```

3）移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：

```java
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

修改后：


```java
@SpringBootApplication
```

### Elasticsearch 搜索引擎

1）修改 `application.yml` 的 Elasticsearch 配置为你自己的：

```yml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
```

2）复制 `sql/post_es_mapping.json` 文件中的内容，通过调用 Elasticsearch 的接口或者 Kibana Dev Tools 来创建索引（相当于数据库建表）

```
PUT post_v1
{
 参数见 sql/post_es_mapping.json 文件
}
```

这步不会操作的话需要补充下 Elasticsearch 的知识，或者自行百度一下~

3）开启同步任务，将数据库的帖子同步到 Elasticsearch

找到 job 目录下的 `FullSyncPostToEs` 和 `IncSyncPostToEs` 文件，取消掉 `@Component` 注解的注释，再次执行程序即可触发同步：

```java
// todo 取消注释开启任务
//@Component
```
前端：
    1 基于 Ant Design Pro 脚手架快速搭建初始项目，并根据业务定制项目模板，如封装
全局异常处理逻辑。
    2 使用 TypeScript + ESLint + Prettier + Husky 保证项目编码和提交规范，提高项目
质量。
    3 使用 Umi OpenAPI 插件，根据后端 Swagger 接口文档自动生成请求 service 层代
码，大幅提高开发效率。
    4 选用兼容性较好的 Echarts 库，接收后端 AI 生成的动态 json 自动渲染可视化图表

后端：

   （业务流程：）后端自定义 Prompt 预设模板并封装用户输入的数据和分析诉求，通
过对接 AIGC 接口生成可视化图表 json 配置和分析结论，返回给前端渲染。
    2 由于 AIGC 的输入 Token 限制，使用 Easy Excel 解析用户上传的 XLSX 表格数据文
件并压缩为 CSV，实测提高了 20% 的单次输入数据量、并节约了成本。
    3 为保证系统的安全性，对用户上传的原始数据文件进行了后缀名、大小、内容等多重
校验（其实能被 Easy Excel 解析成功，内容基本是没问题的）
    4 为防止某用户恶意占用系统资源，基于 Redisson 的 RateLimiter 实现分布式限流，
控制单用户访问的频率。
    5 考虑到单个图表的原始数据量较大，基于 MyBatis + 业务层构建自定义 SQL 实现了
对每份原始数据的分表存储，提高查询性能 XX %（可以实测一下，把所有图表的大
量数据一起 select by Id，和直接 select from 单张数据表）和系统的可扩展性。
    6 由于 AIGC 的响应时间较长，基于自定义 IO 密集型线程池（要能讲清楚为什么是 IO
密集型线程池）+ 任务队列实现了 AIGC 的并发执行和异步化，提交任务后即可响应
前端，提高用户体验。（或者说：支持更多用户排队而不是无限给系统压力导致提交
失败）
    7 由于本地任务队列重启丢失数据，使用 RabbitMQ（分布式消息队列）来接受并持久
化任务消息，通过 Direct 交换机转发给解耦的 AI 生成模块消费并处理任务，提高了
系统的可靠性。

其他（几乎万用）：
    1 基于自己二次开发的 Spring Boot 初始化模板 + MyBatis X 插件，快速生成图表、用
户数据的增删改查
    2 使用 Knife4j + Swagger 自动生成后端接口文档，并通过编写 ApiOperation 等注解
补充接口注释，避免了人工编写维护文档的麻烦