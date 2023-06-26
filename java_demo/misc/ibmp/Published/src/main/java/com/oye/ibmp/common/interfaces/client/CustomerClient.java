package com.oye.ibmp.common.interfaces.client;

import com.oye.ibmp.common.interfaces.facade.CustomerFacade;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(contextId = "server.customer", name = "server", path = "${app.adaptors.services.server.urls.customer}")
public interface CustomerClient extends CustomerFacade {
}
