package com.oyealex.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author oye
 * @since 2020-05-14 23:42:58
 */
@Slf4j
@EnableJms
@EnableAsync
@MapperScan(basePackages = {"com.oyealex.server.mapper"})
@SpringBootApplication
@Getter
public class ServerApplication {
    private static ApplicationContext CONTEXT;

    public static void main(String[] args) {
        CONTEXT = SpringApplication.run(ServerApplication.class);
    }
}
