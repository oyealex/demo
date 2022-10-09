package com.oye.ibmp.common.interfaces.facade;

import com.oye.ibmp.common.interfaces.entity.CustomerDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface CustomerFacade {
    @PostMapping("/random")
    CustomerDto random();

    @GetMapping("/query/{id}")
    CustomerDto query(@PathVariable("id") String id, @RequestParam("name") String name);
}
