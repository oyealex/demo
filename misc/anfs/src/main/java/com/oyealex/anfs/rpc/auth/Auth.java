/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc.auth;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * Credential
 * <pre><code>
 * struct opaque_auth {
 *     auth_flavor flavor;
 *     opaque body<400>;
 * };
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
public abstract class Auth {
    @Getter
    protected final int flavor;

    protected Auth(int flavor) {
        this.flavor = flavor;
    }

    public abstract int writeBody(ByteBuf buf);

    public void write(ByteBuf buf) {
        buf.writeInt(flavor);
        buf.writeInt(0); // reserved for length
        int bodyLength = writeBody(buf);
        // rewrite body length
        buf.writerIndex(buf.writerIndex() - bodyLength - 4);
        buf.writeInt(bodyLength);
        buf.writerIndex(buf.writerIndex() + bodyLength);
    }
}
