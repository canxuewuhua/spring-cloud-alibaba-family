package com.huolieniao.springcloud.alibaba.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;

/**
 * 服务提供者9003 提供的远程调用方法
 */
@RestController
@Slf4j
public class PaymentController
{
    @Value("${server.port}")
    private String serverPort;

    public static HashMap<Long,String> hashMap = new HashMap<>();
    static
    {
        hashMap.put(1L,"1L, 28a8c1e3bc2742d8848569891fb42181");
        hashMap.put(2L,"2L  bba8c1e3bc2742d8848569891ac32182");
        hashMap.put(3L,"3L  6ua8c1e3bc2742d8848569891xt92183");
    }

    @GetMapping(value = "/paymentSQL/{id}")
    public String paymentSQL(@PathVariable("id") Long id)
    {
        String payment = hashMap.get(id);
        String result = "code 200 from mysql,serverPort:  "+serverPort+" "+payment;
        return result;
    }

    @GetMapping(value = "/payment/query/{id}")
    public String queryPaymentById(@PathVariable("id") Long id){
        log.info("服务：" + serverPort + " 提供的feign接口PaymentController.queryPaymentById");
        String payment = hashMap.get(id);
        return payment;
    }

}
