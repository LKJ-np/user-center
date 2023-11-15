# user-center
# 后端技术：

Spring+SpringMVC+SpringBoot框架

Mybatis+Mybatis Plus数据访问框架

MySQL数据库

jUnit单元测试类
# 后端学到的技术：
1.选用MyBatis + MyBatis-Plus进行数据访问层开发，复用大多数通用方法，并且通过继承定制了自己的**通用操作模板**，大幅提升了项目开发效率。

2.为了明确接口的返回，**自定义统一的错误码**，并封装了**全局异常处理器**，从而规范了异常返回、屏蔽了项目冗余的报错细节。

3．对于项目中的JSON格式化处理对象，采用**双检锁单例模式**进行管理，从而复用对象，避免了重复创建对象的开销，便于集中维护管理。(双检锁单例模式的实现非常简单，大家百度一下示例代码即可)

4．采用Nginx完成前端项目部署、采用Docker容器完成后端项目部署，并且使用宝塔面板对项目进行运维监控

5.使用JUnit Jupiter API的@Test注解和Assertions口类实现对用户模块的单元测试，测试覆盖度达到90%

6．通过Spring Boot的多套application-{env}.yml配置文件实现多环境，并通过指定--spring.profiles.active=prod 实现生产环境部署。

7.使用Nginx网关统一接受前端页面和后端接口请求，并通过其proxy pass反向代理配置解决跨域问题。
# 部署：

Linux单机部署

Nginx Web服务器

Docker服务器

容器托管平台
# 前端启动：
yarn
yarn start
