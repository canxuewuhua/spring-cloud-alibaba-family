package com.huolieniao.springcloud.baomidou.service;

import com.huolieniao.springcloud.common.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommonService {

    @Autowired
    private IEmployeeService employeeService;

    public ResultDTO getEmployeeCount(){
        int count = employeeService.count();
        return new ResultDTO("employee表数量为"+count);
    }
}
