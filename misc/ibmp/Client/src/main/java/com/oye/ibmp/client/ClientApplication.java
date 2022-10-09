package com.oye.ibmp.client;

import com.oye.ibmp.common.interfaces.client.CustomerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.oye.ibmp")
public class ClientApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ClientApplication.class, args);
        log.info("==> customer random: {}", context.getBean(CustomerClient.class).random());
        log.info("==> customer query: {}", context.getBean(CustomerClient.class).query("id", "name"));
    }
}
