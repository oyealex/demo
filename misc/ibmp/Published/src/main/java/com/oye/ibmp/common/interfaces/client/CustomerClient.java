package com.oye.ibmp.common.interfaces.client;

import com.oye.ibmp.common.interfaces.facade.CustomerFacade;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(contextId = "customer", name = "server", path = "${app.feign.server.customer.url}")
public interface CustomerClient extends CustomerFacade {
}
