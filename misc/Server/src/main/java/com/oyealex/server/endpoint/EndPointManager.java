/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.endpoint;

import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * TODO 2020/8/30 The EndPointManager
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/30
 */
@Component
public class EndPointManager {
    @Getter
    private final EndPoint local = new EndPoint("localhost", 8080);
}
