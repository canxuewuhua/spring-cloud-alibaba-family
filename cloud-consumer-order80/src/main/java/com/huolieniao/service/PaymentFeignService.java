package com.huolieniao.service;

import com.huolieniao.common.ResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * fallback = PaymentFallbackService.class 只针对提供侧的服务宕机或者关闭 进行兜底的
 */
@Component
@FeignClient(value = "nacos-payment-provider", fallback = PaymentFallbackService.class)
public interface PaymentFeignService
{
    @GetMapping("/check/count")
    ResultDTO getEmployeeCount();

    @GetMapping("/payment/hystrix/ok/{id}")
    String paymentInfo_OK(@PathVariable("id") Integer id);

    @GetMapping("/payment/hystrix/timeout/{id}")
    String paymentInfo_TimeOut(@PathVariable("id") Integer id);

    @GetMapping("/payment/employee")
    String getPaymentEmp();

    //====服务熔断
    @GetMapping("/payment/circuit/{id}")
    String paymentCircuitBreaker(@PathVariable("id") Integer id);
}
