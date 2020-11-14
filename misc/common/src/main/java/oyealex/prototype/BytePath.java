/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package oyealex.prototype;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * BytePath
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-25
 */
@RequiredArgsConstructor
public final class BytePath {
    public static final Charset CHARSET = Charset.defaultCharset();

    private static final byte DOT = '.';

    private static final byte SEPARATOR = '/';

    private static final byte[] EMPTY_PATH = {};

    private static final BytePath EMPTY = new BytePath(EMPTY_PATH, new int[0]);

    private final byte @NotNull [] path;

    private final int @NotNull [] offsets;

    private String cachedPathString;

    private int cachedHashCode = Integer.MIN_VALUE;

    @NotNull
    public BytePath resolve(@NotNull BytePath other) {
        if (isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        return resolve(this, other);
    }

    @NotNull
    public BytePath relativize(@NotNull BytePath other) {
        if (isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        return relativize(this, other);
    }

    @Contract(value = "-> new", pure = true)
    public byte @NotNull [] getPath() {
        return Arrays.copyOf(path, path.length);
    }

    public boolean isEmpty() {
        return path.length == 0;
    }

    public int getNameCount() {
        return offsets.length;
    }

    private int getNameEndIndex(int nameIndex) {
        if (offsets.length == 0 || nameIndex < 0 || nameIndex >= offsets.length) {
            return -1;
        }
        int offset = offsets[nameIndex];
        return offset < 0 ? -offset : offset;
    }

    private int getNameStartIndex(int nameIndex) {
        if (offsets.length == 0 || nameIndex < 0 || nameIndex >= offsets.length) {
            return -1;
        }
        if (nameIndex == 0) {
            return 0;
        }
        int offset = offsets[nameIndex - 1];
        return (offset < 0 ? -offset : offset) + 2;
    }

    @NotNull
    public String toPath() {
        if (cachedPathString == null) {
            cachedPathString = new String(path, CHARSET);
        }
        return cachedPathString;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {return true;}
        if (other == null || getClass() != other.getClass()) {return false;}
        BytePath bytePath = (BytePath) other;
        return Arrays.equals(path, bytePath.path);
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == Integer.MIN_VALUE) {
            cachedHashCode = Arrays.hashCode(path);
        }
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return toPath();
    }

    public String toDebugString() {
        return this + "; " + Arrays.toString(offsets);
    }

    @NotNull
    public static BytePath get(@NotNull String path) {
        return get(path.getBytes(CHARSET));
    }

    @NotNull
    public static BytePath get(byte @NotNull [] path) {
        return normalize(path);
    }

    @NotNull
    public static BytePath empty() {
        return EMPTY;
    }

    private static BytePath normalize(byte[] path) {
        int length = path.length;
        if (length == 0) {
            return EMPTY;
        }
        final int[] nameRangeStack = new int[length + 1]; // 文件名称索引栈，存放各级目录的开始结束索引位置，-1表示为不可解析的父目录
        int stackIndex = -1; // 当前已写入数据的栈顶索引，指向文件名称结束索引值所在的位置
        byte pre = SEPARATOR; // 前一个字节
        int nameStart = 0; // 文件名称起始位置，包含
        int nameEnd; // 文件名称结束位置，包含
        int resultLength = 0; // 保存结果需要的字节长度
        for (int index = 0; index <= length; index++) {
            // 多向后扫描一位，用分隔符补尾，以结束最后一个文件名
            byte curr = index == length ? SEPARATOR : path[index];
            if (pre == SEPARATOR) {
                if (curr == SEPARATOR) {
                    continue; // 连续的分隔符，忽略
                } else {
                    nameStart = index; // 识别到文件名起始位置
                }
            } else {
                if (curr == SEPARATOR) { // 识别到文件名结束位置
                    nameEnd = index - 1;

                    // 当前文件名为“.”，表示当前目录，忽略
                    if (nameStart == nameEnd && path[nameStart] == DOT) {
                        pre = curr;
                        continue;
                    }

                    // 当前文件名为“..”，表示父目录
                    if (nameEnd - nameStart == 1 && path[nameStart] == DOT && path[nameEnd] == DOT) {
                        if (stackIndex >= 0 && nameRangeStack[stackIndex] != -1) {
                            // 还有正常文件名存在，将正常文件名出栈，并从结果长度中减去出栈的文件名长度，包含分隔符
                            resultLength -= nameRangeStack[stackIndex--] - nameRangeStack[stackIndex--] + 2;
                        } else {
                            // 没有可供出栈的正常文件名，添加不可解析的父目录标记
                            nameRangeStack[++stackIndex] = -1;
                            nameRangeStack[++stackIndex] = -1;
                            resultLength += 3; // 累计结果长度，包含分隔符
                        }
                        pre = curr;
                        continue;
                    }

                    // 正常文件名称，入栈
                    nameRangeStack[++stackIndex] = nameStart;
                    nameRangeStack[++stackIndex] = nameEnd;
                    resultLength += nameEnd - nameStart + 2; // 累计结果长度，包含分隔符
                }
            }
            pre = curr;
        }
        // 最终结果为空
        if (resultLength <= 0) {
            return EMPTY;
        }
        // 解析结果
        // 至少存在一个文件名称，去除末尾的分隔符
        final byte[] result = new byte[--resultLength];
        int resultIndex = resultLength; // 结果写索引
        final int[] offsets = new int[(stackIndex + 1) / 2];
        int offsetIndex = offsets.length; // 结果写索引
        int nameLength;
        // 逆序或正序写入结果均可，这里采用逆序写入，节省一个变量
        while (stackIndex >= 0) {
            nameEnd = nameRangeStack[stackIndex--];
            nameStart = nameRangeStack[stackIndex--];
            if (nameEnd == -1) {
                // 写入不可解析的父目录名称“..”
                result[--resultIndex] = DOT;
                result[--resultIndex] = DOT;
                offsets[--offsetIndex] = -resultIndex - 1;
            } else {
                // 写入正常目录
                nameLength = nameEnd - nameStart + 1;
                System.arraycopy(path, nameStart, result, resultIndex -= nameLength, nameLength);
                offsets[--offsetIndex] = resultIndex + nameLength - 1;
            }
            // 如果还有更多的目录层级，插入分隔符
            if (stackIndex >= 0) {
                result[--resultIndex] = SEPARATOR;
            }
        }
        return new BytePath(result, offsets);
    }

    private static BytePath resolve(BytePath base, BytePath child) {
        return EMPTY;
    }

    private static BytePath mergeSubPath(BytePath base, int baseEndNameIndex, BytePath child, int childStartNameIndex) {
        int baseLength = base.getNameEndIndex(baseEndNameIndex) + 1;
        int childLength = child.path.length - child.getNameStartIndex(childStartNameIndex);
        byte[] path = new byte[baseLength + childLength];
        System.arraycopy(base.path, 0, path, 0, baseLength);
        System.arraycopy(child.path, child.path.length - childLength, path, baseLength + 1, childLength);
        path[baseLength] = SEPARATOR;
        int[] offsets = new int[0];
        System.arraycopy(base.offsets, 0, offsets, 0, baseEndNameIndex + 1);
        for (int i = childStartNameIndex; i < child.offsets.length; i++) {
            int offset = child.offsets[i];
            if (offset < 0) {
                // offsets[] = ;
            }
        }
        return new BytePath(path, offsets);
    }

    private static int getNameEndIndex(@NotNull BytePath path, int nameIndex) {
        if (nameIndex < 0) {
            return -1;
        }
        if (nameIndex >= path.offsets.length - 1) {
            return path.path.length - 1;
        }
        int offset = path.offsets[nameIndex + 1];
        return offset < 0 ? -offset - 3 : offset - 2;
    }

    private static BytePath relativize(BytePath bytePath, BytePath other) {
        return EMPTY;
    }

    public static void main(String[] args) {
        BytePath path = BytePath.get("../../123");
        System.out.println(path.getNameStartIndex(-1) + " " + path.getNameEndIndex(-1));
        System.out.println(path.getNameStartIndex(0) + " " + path.getNameEndIndex(0));
        System.out.println(path.getNameStartIndex(1) + " " + path.getNameEndIndex(1));
        System.out.println(path.getNameStartIndex(2) + " " + path.getNameEndIndex(2));
        System.out.println(path.getNameStartIndex(3) + " " + path.getNameEndIndex(3));
    }
}
