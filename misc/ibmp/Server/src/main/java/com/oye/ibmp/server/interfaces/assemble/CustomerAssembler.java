package com.oye.ibmp.server.interfaces.assemble;

import com.oye.ibmp.common.domain.common.entity.vo.CustomerVo;
import com.oye.ibmp.common.interfaces.entity.CustomerDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Customer Assembler
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-12-04
 */
@Component
public class CustomerAssembler {
    public CustomerDto toDto(CustomerVo vo) {
        CustomerDto dto = new CustomerDto();
        BeanUtils.copyProperties(vo, dto);
        return dto;
    }
}
