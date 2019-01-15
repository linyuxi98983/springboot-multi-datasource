package com.imooc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({ "com.imooc.dao.mapper" })
@SpringBootApplication
public class SpringbootMultiDatasourceApplication
{
    
    public static void main(String[] args)
    {
        SpringApplication.run(SpringbootMultiDatasourceApplication.class, args);
    }
}
