

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
