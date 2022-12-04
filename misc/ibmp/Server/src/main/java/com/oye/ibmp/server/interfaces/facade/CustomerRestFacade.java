package com.oye.ibmp.server.interfaces.facade;

import com.oye.ibmp.common.interfaces.entity.CustomerDto;
import com.oye.ibmp.common.interfaces.facade.CustomerFacade;
import com.oye.ibmp.server.application.CustomerCacheService;
import com.oye.ibmp.server.interfaces.assemble.CustomerAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.url.base}/customer")
public class CustomerRestFacade implements CustomerFacade {
    private final CustomerCacheService service;

    private final CustomerAssembler assembler;

    @Override
    public CustomerDto query(String id) {
        CustomerDto customer = assembler.toDto(service.getById(id));
        log.info("query customer: {}", customer);
        return customer;
    }
}
