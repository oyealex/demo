package com.oye.ibmp.server.interfaces.facade;

import com.oye.ibmp.common.interfaces.entity.CustomerDto;
import com.oye.ibmp.common.interfaces.facade.CustomerFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("${app.url.base}/customer")
public class CustomerFacadeImpl implements CustomerFacade {
    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public CustomerDto random() {
        int no = counter.incrementAndGet();
        CustomerDto customer = new CustomerDto().setId("id." + no).setName("name." + no);
        log.info("random customer: {}", customer);
        return customer;
    }

    @Override
    public CustomerDto query(String id, String name) {
        CustomerDto customer = new CustomerDto().setId(id).setName(name);
        log.info("query customer: {}", customer);
        return customer;
    }
}
