package com.huolieniao.springcloud.alibaba.service;

import org.springframework.stereotype.Component;

/**
 *
 * 针对远程feign接口调用 出现服务提供者宕机或者关闭的降级方法处理
 * 即没有一个服务提供者可使用 就会走该降级方法
 */
@Component
public class PaymentFallbackService implements PaymentService
{
    @Override
    public String paymentSQL(Long id)
    {
        return "444 paymentSQL";
    }

    @Override
    public String queryPaymentById(Long id) {
        return "code: 44444 针对服务宕机或者关闭 服务降级返回,---PaymentFallbackService errorSerial";
    }
}
