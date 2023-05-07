package com.huolieniao.controller;

import com.huolieniao.common.ResultDTO;
import com.huolieniao.service.PaymentFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private PaymentFeignService paymentFeignService;

    @GetMapping("msg")
    public String getMsg(){
        return "this is message...";
    }

    @GetMapping("consumer")
    public ResultDTO getEmployeeSize(){
        return paymentFeignService.getEmployeeCount();
    }
}
