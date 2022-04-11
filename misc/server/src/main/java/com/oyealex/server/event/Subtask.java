/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * Subtask
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@Getter
@ToString
@RequiredArgsConstructor
public abstract class Subtask {
    protected final String id;

    public Subtask() {
        this.id = UUID.randomUUID().toString();
    }
}
