/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event;

import lombok.ToString;

/**
 * NfsSubtask
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@ToString(callSuper = true)
public class NfsSubtask extends Subtask {
    public NfsSubtask(String id) {
        super(id);
    }

    public NfsSubtask() {
    }
}
