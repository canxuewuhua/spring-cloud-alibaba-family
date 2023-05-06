package com.huolieniao.springcloud.dao;

import com.huolieniao.springcloud.entities.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PersonDao {

    Person getPersonById(@Param("id") Integer id);
}
