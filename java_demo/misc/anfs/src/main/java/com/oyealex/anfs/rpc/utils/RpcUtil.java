/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RpcMarkUtil
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcUtil {
    public static final int LAST_FRAGMENT_MASK = 0x80000000;

    public static final int FRAGMENT_LENGTH_MASK = 0x7FFFFFFF;

    public static boolean isLastFragment(long fragmentHeader) {
        return (fragmentHeader & LAST_FRAGMENT_MASK) != 0;
    }

    public static long getFragmentLength(long fragmentHeader) {
        return fragmentHeader & FRAGMENT_LENGTH_MASK;
    }
}
