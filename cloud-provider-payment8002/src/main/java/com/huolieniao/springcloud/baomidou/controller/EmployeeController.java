package com.huolieniao.springcloud.baomidou.controller;

import com.huolieniao.springcloud.baomidou.service.CommonService;
import com.huolieniao.springcloud.common.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("check")
public class EmployeeController {

    @Autowired
    private CommonService commonService;

    /**
     * 运行结果：
     * {"code":100,"message":"成功","data":"employee表数量为15
     * person表的id=1的数据为Person(personId=1, firstName=san, lastName=zhang)"}
     * 结论说明： 程序中使用mapper的xml方式和使用baomidou mybatis-plus同时使用是没有问题的
     */
    @GetMapping("count")
    public ResultDTO getEmployeeCount(){
        return commonService.getEmployeeCount();
    }
}
