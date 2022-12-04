package com.oye.ibmp.common.domain.common.entity.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * CustomerVo
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-12-04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CustomerVo {
    private String id;

    private String name;
}
