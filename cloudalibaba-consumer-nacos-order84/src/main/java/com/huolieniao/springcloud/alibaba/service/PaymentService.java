package com.huolieniao.springcloud.alibaba.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "nacos-payment-provider",fallback = PaymentFallbackService.class)
public interface PaymentService {

    @GetMapping(value = "/paymentSQL/{id}")
    String paymentSQL(@PathVariable("id") Long id);

    @GetMapping(value = "/payment/query/{id}")
    String queryPaymentById(@PathVariable("id") Long id);
}
