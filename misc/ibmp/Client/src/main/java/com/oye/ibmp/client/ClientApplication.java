package com.oye.ibmp.client;

import com.oye.ibmp.common.interfaces.client.CustomerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 客户端启动类
 *
 * @author oyealex
 * @since 2023-01-18
 */
@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.oye.ibmp")
@EnableFeignClients(clients = {CustomerClient.class})
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
