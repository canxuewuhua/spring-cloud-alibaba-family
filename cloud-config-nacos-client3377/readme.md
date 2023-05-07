


该启动项目引入依赖 spring-cloud-starter-alibaba-nacos-config
个人认为该项目也可以不需要 就是在各自的项目中引入该依赖 并使用相应环境的nacos配置

通常一个系统会准备
dev环境
test环境
prod环境
namespace+group+dataID 
namespace 区分部署环境  group和dataId逻辑上区分目标
namespace=public Group=DEFAULT_GROUP 默认Cluster是DEFAULT
NACOS默认的命名空间是public  namespace主要用来实现隔离
比方说我们现在有三个环境：开发 测试 生产环境  我们就可以创建三个namespace 不同的namespace之间是隔离的
Group 默认是DEFAULT_GROUP   Group可以把不同的微服务划分到同一个分组里面去

Service就是微服务 一个Service可以包含多个Cluster集群  Nacos默认的Cluster是DEFAULT
Cluster是对微服务的一个虚拟划分
比方说为了容灾 将Service微服务分别部署在了杭州机房和广州机房
这时就可以给杭州机房的Service微服务起一个集群名称（HZ）
给广州机房的Service微服务起一个集群名称（GZ） 还可以尽量让一个机房的微服务互相调用 以提升性能

在nacos配置台上 先新建一个命名空间 如dev test prod
那么在配置列表上就有了 public dev test prod
那么Data Id: nacos-config-client-dev.yaml
Group: DEV_GROUP

下一步就是nacos的集群配置以及持久化配置 nacos自带derby数据库重启后配置会丢失 可以考虑使用mysql
https://nacos.io/zh-cn/docs/what-is-nacos.html  可以观看官网的配置  
https://nacos.io/zh-cn/docs/deployment.html 配置数据库 由自带derby数据库切换到mysql数据库

记录一个坑 当前使用的nacos版本为1.1.4 内置的数据库版本可能低 如果本机安装的是mysql 8.0.28
那么需要在nacos的安装目录下 即nacos下 新建一个文件夹 plugins/mysql 
将/Users/zhuyq/repository/judy_repository/mysql/mysql-connector-java 选一个8.0版本的jar 8.0.22放到上述文件下
才能正常启动nacos 否则会报错（在移植数据库的时候 由内置derby数据库迁移至mysql数据库时需注意）

需要将nacos文件下的nacos-mysql.sql脚本在自己的数据库上执行 新建一个数据库nacos_config
同时在application.properties文件的末尾添加

spring.datasource.platform=mysql

db.num=1
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?useSSL=false&serverTimezone=Hongkong&characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.user=root
db.password=123456
之后就可以正常配置 且配置信息会记录在mysql数据库 即使nacos重启了 配置不会丢失
看linux服务器ip  hostname - i 

考虑到nacos生产环境需要配置集群 且数据库需要是mysql数据库
所以需要配置nacos下的cluster文件 将nacos配置为集群模式 且设置不同端口号或者不同的服务器地址端口号
nacos/conf/cluster.conf 中配置 192.168.111.144：3333  192.168.111.144：4444  192.168.111.144：5555
接着编辑nacos的startup.sh文件，使它能够接受不同的启动端口
单机版的都是都是 ./startup.sh -m standalone

集群启动 我们希望可以类似其他软件的shell命令 传递不同的端口号启动不同的nacos实例
命令  ./startup.sh -p 3333 表示启动端口为3333的nacos服务器实例


nginx的配置 由它作为负载均衡器
具体配置可以参考周阳老师讲解 及有道笔记的nginx配置

大体逻辑就是 首先启动三个nacos 客户端
然后配置nginx 有nginx路由转发到不同的ncaos客户端 启动nginx
浏览器地址栏只需访问nginx地址及端口就可以动态路由至三个不同的nacos客户端 进行负载均衡路由


一个Nginx + 3个nacos注册中心  + 1个mysql
