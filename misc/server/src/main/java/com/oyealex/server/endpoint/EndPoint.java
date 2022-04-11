/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.endpoint;

import lombok.Data;

/**
 * TODO 2020/8/30 The EndPoint
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Data
public class EndPoint {
    private final String host;

    private final int port;

    public String getUrlLeadingWithSchema(String schema) {
        return schema + "://" + host + ":" + port;
    }
}
