package com.oye.ibmp.client.interfaces.facade;

import com.oye.ibmp.common.interfaces.client.CustomerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client")
public class ClientFacade {
    private final CustomerClient customerClient;

    @GetMapping("/random")
    public String random() {
        return customerClient.random().toString();
    }

    @GetMapping("/query")
    public String query() {
        return customerClient.query("id").toString();
    }
}
