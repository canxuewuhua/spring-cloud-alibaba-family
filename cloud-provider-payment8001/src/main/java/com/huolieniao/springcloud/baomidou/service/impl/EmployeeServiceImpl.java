package com.huolieniao.springcloud.baomidou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huolieniao.springcloud.baomidou.domain.Employee;
import com.huolieniao.springcloud.baomidou.mapper.EmployeeMapper;
import com.huolieniao.springcloud.baomidou.service.IEmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
}
