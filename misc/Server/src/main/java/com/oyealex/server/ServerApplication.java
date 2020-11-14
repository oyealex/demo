package com.oyealex.server;

import com.oyealex.server.endpoint.EndPointManager;
import com.oyealex.server.entity.Response;
import com.oyealex.server.entity.Student;
import com.oyealex.server.rest.RestUtil;
import com.oyealex.server.rest.uri.UriManager;
import com.oyealex.server.util.ServerContext;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Optional;

/**
 * @author oye
 * @since 2020-05-14 23:42:58
 */
@Slf4j
@EnableAsync
@MapperScan(basePackages = {"com.oyealex.server.mapper"})
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class);

        Optional<Response<Student>> responseOpt =
            ServerContext.getBean(RestUtil.class).get(ServerContext.getBean(EndPointManager.class).getLocal(),
                "/person" + UriManager.STUDENT, null);
        responseOpt.map(Response::getData).ifPresent(responseData -> log.info("type: {}, value: {}",
            responseData.getClass(), responseData));
    }
}
