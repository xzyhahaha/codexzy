package com.codexzy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.codexzy.mapper")
public class CodexzyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexzyApplication.class, args);
    }
}
