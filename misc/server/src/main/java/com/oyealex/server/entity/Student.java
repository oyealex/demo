/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO 2020/8/28 The Student
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/28
 */
@Getter
@Setter
@ToString(callSuper = true)
public class Student extends Person {
    public Student(String name, int age, String grades) {
        super(name, age);
        this.grades = grades;
    }

    private String grades;
}
