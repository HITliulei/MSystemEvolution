package com.septemberhx.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/20
 */
@SpringBootApplication
@MapperScan("com.septemberhx.server.mapper")
public class MOrchestrationServerMain {
    public static void main(String[] args) {
        SpringApplication.run(MOrchestrationServerMain.class, args);
    }
}
