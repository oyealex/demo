/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO 2020/8/28 The Person
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/28
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public abstract class Person {
    private String name;

    private int age;
}
