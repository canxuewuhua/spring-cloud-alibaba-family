

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

注意一个问题：如果在消费侧配置了feign:hystrix:enabled: true 可能原有的在提供侧做降级的代码并没有执行
可能是 要么在服务提供侧做降级   或者是   在消费侧做降级  不要同时做 需要再细作研究

降级方法多了 会造成代码膨胀 及 代码混乱
那么就需要配置全局的降级方法 合理的减少代码量

代码膨胀的问题可以做全局的fallback 让走全局统一的降级方法 可以防止代码过多 同时定制化的降级再具体写
全局的降级配置如下
@DefaultProperties(defaultFallback = "payment_Global_FallBackMethod")
然后    @GetMapping("/payment/employee")
       @HystrixCommand
即可 不需要再写一堆配置 定制化的按照之前的降级配置就行

另外如果提供侧宕机或者关闭了 有没有方法走降级呢？
有的 远程feign接口上设置
@FeignClient(value = "nacos-payment-provider", fallback = PaymentFallbackService.class)
编写一个实现类PaymentFallbackService实现PaymentFeignService接口
在这个实现类中写的方法就是提供侧宕机或者关闭后的降级处理方法
注意如果不是宕机 而是 超时或者错误还是走之前的@HystrixCommand配置

服务熔断既可以做到消费端侧  也可以做在服务端侧
close open half-open
刚开始断路器是关闭的  当大量错误访问报异常了 那么断路器会开启 open
此时的请求会走降级方法 断路器提供了参数  比如说 请求次数、时间窗口、请求失败率
那么就是说 如果配置的在10秒 有6次请求失败 超过了60%错误率那么就会触发熔断 保险丝拉闸断电
 后续的请求都会走降级方法 即使是正确的请求过来也不会响应
待超过了时间窗口 正确率上来了 那么会half-open 进而 close 关闭断路器 正常请求
这是断路器的工作原理

http://localhost/order/payment/circuit/-1 点击多次 超过60%的请求都是异常
"id 不能负数，请稍后再试，/(ㄒoㄒ)/~~ id: -1"

http://localhost/order/payment/circuit/1 即使输入正确的参数1了也会报上面的错误信息
待等一段时间后 正确的参数1才会得到正确的响应结果！！！


-------------- 此时只使用了一个提供侧8001  另外一个提供侧8002可以照着8001就行编写

《学习微服务组件网关 GateWay》
GateWay是springcloud出的 因为等不及nexflix的zuul2 自己开发的一套网关组件
新建一个模块 cloud-gateway-gateway9527
因为该模块是网关 不需要SpringBoot-web

网关的三大核心概念  路由 断言 过滤器

动态路由 route
断言 predicates 就是对一些规则进行匹配 比如说请求头里需要带哪些信息 比如说请求发起的时间限制 header cookie path
过滤器 filters 的作用就更大了 对请求参数和响应参数额外添加一些信息 还有
* 自定义全局GlobalFilter 作用
* 全局日志记录  比如说在请求之前加上一些处理或者日志打印
* 统一网关鉴权  比如说对参数进行校验 鉴权操作

下载sentinel1.7.0  使用命令 java -jar sentinel-dashboard-1.7.0.jar &   
假设8080端口被占用了怎么办？
mac解决端口被占用的方法为：
sudo lsof -i tcp:8080  需要开机密码的 会需要输入开机密码
之后
COMMAND   PID  USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
java    23489 zhuyq  132u  IPv6 0xc361fb5080dda065      0t0  TCP *:http-alt (LISTEN)
表示端口被占用 那么使用kill -9 23489 杀死该进程 就会被释放8080端口


sentinel

======流控======
对请求设置流控规则
比如说QPS设置为1 则一秒一次可以 一秒多次就是出现 "Blocked by Sentinel (flow limiting)"提示
![img_1.png](img_1.png)

QPS是在外面的请求  线程数是进入到服务内 如银行网点 但是数量有限
一个叫御敌于国门之外 一个叫做关门打狗

B惹事 A挂

预热 warmup  刚开始3个请求 5秒后 就允许10个请求了
流控效果中的排队等待 即一秒一个

======降级========
sentinel的断路器是没有半开状态的 可能新版本有半开状态
RT 异常比例 异常数三个降级策略

热点规则 类似hystrix的降级方法fallback
blockHandler 兜底方法

控制台配置的违规行为 @SentinelResource来管
但是异常程序的错误blockHandler兜底方法处理不了 而是通过fallback来处理

fallback管运行异常
blockHandler管配置违规
若blockHandler和 fallback都进行了配置 则被限流降级而抛出BlockException时只会进入blockHandler处理逻辑
exceptionToIgnore={IllegalArgumentException.class} 如果设置忽略则不会考虑该异常的fallback

sentinel的持久化是持久化到nacos的 一个是依赖sentinel-datasource-nacos
再一个是在配置文件中 配置 sentinel：datasource

考虑再建一个工程 使用nacos openFeign sentinel的案例使用
还有一个分布式事务 seata的案例使用

sentinel再学习
fallback管运行异常
blockHandler管配置违规
blockHandler既可以在同一个类中写方法 也可以指定类 并指定类的方法

today 模拟consumer84服务 负载均衡调用服务提供者9003和9004 通过openFeign进行负载均衡调用
通过验证@SentinelResource注解的 fallback和blockHandler 一个针对异常降级处理 一个是针对流控的降级处理
想要sentinel持久化 还需在nacos上配置信息 记录持久化信息 这个暂时没有在nacos上配置 也很简单 后续使用到了再配置

sentinel控制台上可以配置的信息有：
针对接口路径客户已配置 流控 降级 热点key限流 
流控里面有根据QPS 还是根据线程数  配置单机阈值
流控模式 直接 关联 链路
流控效果 快速失败 warm up 排队等待

降级里面 有 资源名  配置降级策略： RT 异常比例 异常数  还有就是时间窗口进行组合

热点规则 有 资源名 限流模式QPS 参数索引 单机阈值 统计窗口时长 是否集群 




服务降级 服务熔断
服务降级是针对异常或者超过阈值 返回一个温馨友好的提示
服务熔断是当超过阈值 拉闸限电 此时即使正常的数据也会走降级方法  待过了时间窗口正常的数据多了 会关闭断路器正常执行







