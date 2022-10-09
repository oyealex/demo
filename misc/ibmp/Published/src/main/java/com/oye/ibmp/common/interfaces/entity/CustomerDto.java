package com.oye.ibmp.common.interfaces.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CustomerDto {
    private String id;

    private String name;
}
