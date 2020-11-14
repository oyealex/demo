/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.controller;

import com.oyealex.server.entity.Response;
import com.oyealex.server.entity.Student;
import com.oyealex.server.entity.Teacher;
import com.oyealex.server.rest.uri.UriManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO 2020/8/30 The PersonController
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {
    @GetMapping(UriManager.STUDENT)
    public Response<Student> getStudent() {
        return Response.success(new Student("alex", 19, "grade-1"));
    }

    @GetMapping({UriManager.STUDENT_BY_ID, UriManager.STUDENT_BY_ID_2})
    public Response<Student> getStudent(@PathVariable String id) {
        return Response.success(new Student(id, 19, "grade-1"));
    }

    @RequestMapping(method = RequestMethod.GET, path = UriManager.TEACHER)
    public Response<Teacher> getTeacher() {
        return Response.success(new Teacher("Jack", 34, "English"));
    }

    @RequestMapping("/method1")
    public Response<Teacher> method1() {
        return Response.success(new Teacher("Jack", 34, "English"));
    }
}
