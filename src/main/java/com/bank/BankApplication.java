package com.bank;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** 银行网点管理系统：Spring Boot 2.7 + MyBatis-Plus + Thymeleaf + Vue3。IDEA 运行本类，访问 http://localhost:8080/login */
@SpringBootApplication
@MapperScan("com.bank.dao")
public class BankApplication {
    public static void main(String[] args) { SpringApplication.run(BankApplication.class, args); }
}
