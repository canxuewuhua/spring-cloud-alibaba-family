package com.huolieniao.controller;

import cn.hutool.core.util.IdUtil;
import com.huolieniao.common.ResultDTO;
import com.huolieniao.service.PaymentFeignService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
@DefaultProperties(defaultFallback = "payment_Global_FallBackMethod")
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

    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1500")
    })
    public String consumer_PaymentInfo_TimeOut(@PathVariable("id") Integer id)
    {
        int age = 10/0;
        String result = paymentFeignService.paymentInfo_TimeOut(id);
        return result;
    }
    public String paymentTimeOutFallbackMethod(@PathVariable("id") Integer id)
    {
        return "我是消费者80,对方支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
    }

    // 使用全局的fallback案例
    @GetMapping("/payment/employee")
    @HystrixCommand
    public String getPaymentEmp(){
//        int age = 10/0;
        return paymentFeignService.getPaymentEmp();
    }

    // 该方法是全局的fallback方法
    public String payment_Global_FallBackMethod(){
        return "Global异常处理信息，请稍后再试 o(╥﹏╥)o";
    }

    @GetMapping("/payment/employee2")
    public String getPaymentEmp2(){
        int age = 10/0;
        return paymentFeignService.getPaymentEmp();
    }

    /**
     * 服务熔断案例
     * @param id
     * @return
     */
    @GetMapping("/payment/circuit/{id}")
    public String paymentCircuitBreaker(@PathVariable("id") Integer id){
        return paymentFeignService.paymentCircuitBreaker(id);
    }

    /**
     * 服务熔断既可以做到消费端侧  也可以做在服务端侧
     */

//    //====服务熔断
//    @GetMapping("/payment/circuit/{id}")
//    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
//            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
//            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
//            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
//            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸
//    })
//    public String paymentCircuitBreaker(@PathVariable("id") Integer id)
//    {
//        if(id < 0)
//        {
//            throw new RuntimeException("******id 不能负数");
//        }
//        String serialNumber = IdUtil.simpleUUID();
//
//        return Thread.currentThread().getName()+"\t"+"调用成功，流水号: " + serialNumber;
//    }
//    public String paymentCircuitBreaker_fallback(@PathVariable("id") Integer id)
//    {
//        return "id 不能负数，请稍后再试，/(ㄒoㄒ)/~~   id: " +id;
//    }
}
