/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc.utils;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * XdrUtil
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XdrUtil {
    /**
     * Padding to 4
     *
     * @param length actual length
     * @return bytes length need to padding
     */
    public static int getPaddingBytes(int length) {
        return (4 - (0x03 & length)) & 0x03;
    }

    /**
     * Read String
     * <p/>
     * The standard defines a string of n (numbered 0 through n-1) ASCII
     * bytes to be the number n encoded as an unsigned integer (as described
     * above), and followed by the n bytes of the string. Byte m of the
     * string always precedes byte m+1 of the string, and byte 0 of the
     * string always follows the string’s length. If n is not a multiple of
     * four, then the n bytes are followed by enough (0 to 3) residual zero
     * bytes, r, to make the total byte count a multiple of four. Counted
     * byte strings are declared as follows:
     * <pre><code>
     *     string object<m>;
     * or
     *     string object<>;
     * </code></pre>
     * The constant m denotes an upper bound of the number of bytes that a
     * string may contain. If m is not specified, as in the second
     * declaration, it is assumed to be (2**32) - 1, the maximum length.
     * The constant m would normally be found in a protocol specification.
     * For example, a filing protocol may state that a file name can be no
     * longer than 255 bytes, as follows:
     * <pre><code>
     * string filename<255>;
     *
     *    0     1     2     3     4     5   ...
     * +-----+-----+-----+-----+-----+-----+...+-----+-----+...+-----+
     * |        length n       |byte0|byte1|...| n-1 |  0  |...|  0  |
     * +-----+-----+-----+-----+-----+-----+...+-----+-----+...+-----+
     * |<-------4 bytes------->|<------n bytes------>|<---r bytes--->|
     *                         |<----n+r (where (n+r) mod 4 = 0)---->|
     *                                                          STRING
     * </code></pre>
     * It is an error to encode a length greater than the maximum described
     * in the specification.
     *
     * @param str string to write
     * @param buf dst buf
     * @return actual written bytes length
     */
    public static int writeString(String str, ByteBuf buf) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int padding = getPaddingBytes(bytes.length);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.writeZero(padding);
        return 4 + bytes.length + padding;
    }

    /**
     * Write an int array
     *
     * @param array int array to write
     * @param buf   dst buf
     * @return actual written bytes length
     */
    public static int writeIntArray(int[] array, ByteBuf buf) {
        buf.writeInt(array.length);
        for (int ele : array) {
            buf.writeInt(ele);
        }
        return 4 + array.length * 4;
    }

    /**
     * Read Variable-length Opaque Data
     * <p/>
     * The standard also provides for variable-length (counted) opaque data,
     * defined as a sequence of n (numbered 0 through n-1) arbitrary bytes
     * to be the number n encoded as an unsigned integer (as described
     * below), and followed by the n bytes of the sequence.
     * <p/>
     * Byte m of the sequence always precedes byte m+1 of the sequence, and
     * byte 0 of the sequence always follows the sequence’s length (count).
     * If n is not a multiple of four, then the n bytes are followed by
     * enough (0 to 3) residual zero bytes, r, to make the total byte count
     * a multiple of four. Variable-length opaque data is declared in the
     * following way:
     * <pre><code>
     *     opaque identifier<m>;
     * or
     *     opaque identifier<>;
     * </code></pre>
     * The constant m denotes an upper bound of the number of bytes that the
     * sequence may contain. If m is not specified, as in the second
     * declaration, it is assumed to be (2**32) - 1, the maximum length.
     * The constant m would normally be found in a protocol specification.
     * For example, a filing protocol may state that the maximum data
     * transfer size is 8192 bytes, as follows:
     * <pre><code>
     * opaque filedata<8192>;
     *    0     1     2     3     4     5   ...
     * +-----+-----+-----+-----+-----+-----+...+-----+-----+...+-----+
     * |        length n       |byte0|byte1|...| n-1 |  0  |...|  0  |
     * +-----+-----+-----+-----+-----+-----+...+-----+-----+...+-----+
     * |<-------4 bytes------->|<------n bytes------>|<---r bytes--->|
     *                         |<----n+r (where (n+r) mod 4 = 0)---->|
     *                                          VARIABLE-LENGTH OPAQUE
     * </code></pre>
     * It is an error to encode a length greater than the maximum described
     * in the specification.
     *
     * @param buf buffer to read from
     * @return byte array
     */
    public static byte[] readByteArray(ByteBuf buf) {
        int length = buf.readInt();
        byte[] array = new byte[length];
        buf.readBytes(array, 0, length).skipBytes(getPaddingBytes(length));
        return array;
    }

    public static int[] readIntArray(ByteBuf buf) {
        int length = buf.readInt();
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = buf.readInt();
        } // int array is always multiple of 4, no need padding
        return array;
    }

    private static final char[] digest =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String dumpToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte ele : bytes) {
            builder.append(digest[(ele >>> 4) & 0x0F]).append(digest[ele & 0x0F]);
        }
        return builder.toString();
    }
}
