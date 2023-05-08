package com.huolieniao.controller;

import com.huolieniao.common.ResultDTO;
import com.huolieniao.service.PaymentFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/ok/{id}")
    public ResultDTO paymentInfo_OK(@PathVariable("id") Integer id){
        String result = paymentFeignService.paymentInfo_OK(id);
        return new ResultDTO(result);
    }

    @GetMapping("/timeout/{id}")
    public ResultDTO paymentInfo_TimeOut(@PathVariable("id") Integer id){
        String result = paymentFeignService.paymentInfo_TimeOut(id);
        return new ResultDTO(result);
    }
}
