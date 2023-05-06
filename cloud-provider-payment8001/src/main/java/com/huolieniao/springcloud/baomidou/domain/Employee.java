package com.huolieniao.springcloud.baomidou.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("employee")
public class Employee {

    private Integer id;
    private Integer salary;
}
