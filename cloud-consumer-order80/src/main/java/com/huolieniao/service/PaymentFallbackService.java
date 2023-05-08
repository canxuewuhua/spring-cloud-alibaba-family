package com.huolieniao.service;

import com.huolieniao.common.ResultDTO;
import org.springframework.stereotype.Component;

/**
 *该类的处理方式 主要是针对feign接口宕机或者关闭的问题 对其他比如超时或者错误请使用注解@HystrixCommand
 *
 * 服务降级 客户端去调服务端 碰上服务端宕机或者关闭
 *
 * 本次案例服务降级处理是在 客户端80实现完成的 与服务端8001没有关系
 * 只需要为Feign客户端定义的接口添加一个服务降级处理的实现类即可实现解耦
 *
 * 前提是PaymentFeignServicefeign接口上添加
 * @FeignClient(value = "nacos-payment-provider", fallback = PaymentFallbackService.class)
 *
 * 这样就达到了解耦的效果
 */
@Component
public class PaymentFallbackService implements PaymentFeignService
{
    @Override
    public ResultDTO getEmployeeCount() {
        return null;
    }

    @Override
    public String paymentInfo_OK(Integer id)
    {
        return "-------PaymentFallbackService fall back-paymentInfo_OK ,o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id)
    {
        return "-----PaymentFallbackService fall back-paymentInfo_TimeOut ,o(╥﹏╥)o";
    }

    @Override
    public String getPaymentEmp() {
        return "-----PaymentFallbackService fall back-getPaymentEmp2 controller类无需添加    @HystrixCommand 达到解耦效果 ,o(╥﹏╥)o";
    }

    @Override
    public String paymentCircuitBreaker(Integer id) {
        return "-----PaymentFallbackService fall back-paymentCircuitBreaker 服务熔断的案例,o(╥﹏╥)o";
    }
}
