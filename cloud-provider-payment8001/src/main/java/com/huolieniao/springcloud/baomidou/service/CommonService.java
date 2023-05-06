package com.huolieniao.springcloud.baomidou.service;

import com.huolieniao.springcloud.common.ResultDTO;
import com.huolieniao.springcloud.dao.PersonDao;
import com.huolieniao.springcloud.entities.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommonService {

    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private PersonDao personDao;

    public ResultDTO getEmployeeCount(){
        int count = employeeService.count();
        Person person = personDao.getPersonById(1);
        return new ResultDTO("employee表数量为"+count + " person表的id=1的数据为"+person);
    }
}
