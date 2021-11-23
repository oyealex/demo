/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.server.event;

import lombok.ToString;

/**
 * CifsSubtask
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-24
 */
@ToString(callSuper = true)
public class CifsSubtask extends Subtask{
    public CifsSubtask(String id) {
        super(id);
    }

    public CifsSubtask() {
    }
}
