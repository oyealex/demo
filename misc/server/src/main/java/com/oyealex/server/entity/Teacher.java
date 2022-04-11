/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO 2020/8/28 The Teacher
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/28
 */
@Getter
@Setter
@ToString(callSuper = true)
public class Teacher extends Person {
    public Teacher(String name, int age, String subject) {
        super(name, age);
        this.subject = subject;
    }

    private String subject;
}
