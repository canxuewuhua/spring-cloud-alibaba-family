package com.huolieniao.springcloud.baomidou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huolieniao.springcloud.baomidou.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
