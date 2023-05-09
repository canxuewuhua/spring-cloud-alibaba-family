package com.huolieniao.springcloud.alibaba.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.huolieniao.springcloud.alibaba.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * 总结： 是异常兜底处理  还是限流处理 需要看@SentinelResource注解配置的
 * 是 fallback  还是blockHandler ??
 * 如果是fallback就是 程序的异常兜底 比如程序出现了异常或者错误了 不异常兜底那么返给前端的将是大片的error
 *
 * 如果是blockHandler 是在sentinel控制台上配置了流控方案  那么这个blockHandler是针对限流给出的提示
 *
 *      场景分析：正常的请求不会执行fallback的方法，但是正常的请求如果超出了限流方案 即配置违规那么就会走blockHandler
 *      异常的请求 比如说id=4 或5 首先会走fallback的执行方法 当然如果你执行多次超出了限流方案 那么会执行blockHandler方法逻辑
 */
@RestController
@Slf4j
public class CircleBreakerController {

    @Resource
    private PaymentService paymentService;

    /**
     * 一个简单的案例 通过openFeign-ribbon实现负载均衡
     */
    @GetMapping(value = "/consumer/paymentSQL/{id}")
    public String paymentSQL(@PathVariable("id") Long id)
    {
        return paymentService.paymentSQL(id);
    }

    /**
     * 由上述案例升级
     * 使用@SentinelResource
     */
    @GetMapping(value = "/consumer/fallback/{id}")
//    @SentinelResource(value = "fallback") //没有配置
//    @SentinelResource(value = "fallback",fallback = "handlerFallback") //fallback只负责业务异常
//    @SentinelResource(value = "fallback",blockHandler = "blockHandler") //blockHandler只负责sentinel控制台配置违规 如果输入错误的id=4那么也会出现非法参数异常 点击多次触发限流（正常的点多次就会触发限流）
    @SentinelResource(value = "fallback",fallback = "handlerFallback",blockHandler = "blockHandler",
            exceptionsToIgnore = {IllegalArgumentException.class})
    public String fallback(@PathVariable("id") Long id)
    {
        String s = paymentService.queryPaymentById(id);
        if (id == 4){
            throw new IllegalArgumentException ("IllegalArgumentException,非法参数异常....");
        }else if (s == null){
            throw new NullPointerException ("NullPointerException,该ID没有对应记录,空指针异常");
        }
        return s;
    }

    /**
     * 该方法是@SentinelResource注解配置的针对 业务异常的兜底
     */
    public String handlerFallback(@PathVariable  Long id,Throwable e) {
        return "id:"+id+" ===code: 4444, 兜底异常handlerFallback,exception内容  " + e.getMessage();
    }

    /**
     * 该方法是@SentinelResource注解配置的针对 sentinel控制台配置 流控规则超出阈值的限流方案处理
     */
    public String blockHandler(@PathVariable  Long id, BlockException blockException) {
        return "id:"+id+" ===code: 5555, blockHandler-sentinel限流,无此流水: blockException " + blockException.getMessage();
    }
}
