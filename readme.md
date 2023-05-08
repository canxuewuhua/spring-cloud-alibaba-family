

微服务模块构建步骤：
1 建module
2 改pom
3 写YML
4 主启动
5 业务代码块

《苞米豆的使用 简化数据库操作》
项目建立之初，验证了使用mapper的xml方式和苞米豆的mybatis-plus方式访问数据库数据可以同时使用
苞米豆的使用方式是：
1 首先引入 com.baomidou依赖
2 mapper文件下书写接口EmployeeMapper extends BaseMapper<Employee>
3 IEmployeeService extends IService<Employee>
4 EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService 
    备注：commonService中引入IEmployeeService 改IEmployeeService必须要有实现类EmployeeServiceImpl
         而EmployeeServiceImpl又必须继承ServiceImpl 中的EmployeeMapper 所以EmployeeMapper也必须要继承BaseMapper

编码：   interface PersonMapper extends BaseMapper<Person>
        interface IPersonService extends IService<Person>
        class PersonServiceImpl extends ServiceImpl<PersonMapper,Person> implements IPersonService
        其他业务类就可以直接引入IPersonService 进行调用
可以在mapper接口文件中写自定义的sql

《微服务注册中心 eureka consul zookeeper nacos》
dubbo的注册中心可以是zookeeper 也可以使用nacos 心跳检测

nacos注册中心怎么使用？
nacos 是注册中心(替代eureka)+配置中心(替代config)
nacos官网 http://nacos.io
本demo是nacos的 1.1.4 版本
下载nacos1.1.4
在bin目录下执行 ./startup.sh -m standalone
启用nacos客户端后 就不需要像eureka那样建立一个server的项目了 只需要启动nacos的客户端 其他服务注册到nacos就行了
http://localhost:8848/nacos

当前服务 payment8001 payment8002两个提供者 consumer-order80为消费者
此时还没有消费者调用提供者 后续通过openFeign进行调用
当前了解学习nacos的服务配置 命名空间 dataId group分组方案 namespace 以及集群 持久化 nacos的linux安装等相关的学习
Eureka是CP Zookeeper是AP  nacos是既支持CP 也支持AP

cloud-config-nacos-client3377可以不启动 它只是配置中心的使用
Ribbon和nginx区别   nginx是服务端实现的 是档在最外面的  Ribbon是本地负载均衡

Feign已经停更 目前使用最多的就是openFeign

openFeign内置Ribbon 也支持负载均衡 
对于同一个服务名的多个服务的相同接口 会采用轮询方式进行调用
客户端启动类开启@EnableFeignClients  接口上添加@FeignClient(value = "nacos-payment-provider")
openFeign默认等待一秒钟 假如所调用的服务确实时间会长 会超过1秒钟 那么就需要设置openFeign的超时时间
需要再yml文件中设置时间
同时也可以定义 FeignConfig配置且在配置文件中开启debug日志监控

hystrix目前也已经进入了维护模式了
目前国外使用的是resilience4j
国内使用的就是alibaba的sentinel了

fallback 服务降级
break 服务熔断 类似于家里的保险丝 跳闸断电
flow limit 服务限流  秒杀高并发等操作 严禁一窝蜂的过来拥挤 大家排队

2万个请求请求一个超时接口  结果造成正常的接口也被拖累变慢了
原因：tomcat的默认的工作线程数被打满了，没有多余的线程来分解压力和处理了

超时不再等待 友好返回
出错要有兜底处理

hystrix引入到服务提供者 或者服务消费方都是可以的 都可以起到降级的功效  但是一般都是放在消费客户端上做降级
一般引入spring-cloud-starter-netflix-hystrix 依赖就可以

注意点 （很关键）
因为openFeign设置的超时时间为5秒
那么如果在提供者侧hystrix的超时时间设置为5秒 业务执行6秒则不会执行fallback方法
可能是先走了openFeign的超时异常返回了
业务提供者要使用hystrix 需要开启@EnableCircuitBreaker注解

想让消费侧支持hystrix需要做到一下几点
消费侧客户端需要引入hystrix依赖 另外配置文件中添加feign:hystrix:enabled: true  启动类上开启 @EnableHystrix注解

------- 另外注意：为避免运行结果各种问题 建议修改完代码后 重启提供者侧 也重启消费侧  -------
