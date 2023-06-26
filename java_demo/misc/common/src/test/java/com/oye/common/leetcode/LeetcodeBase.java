/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oye.common.leetcode;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 算法测试用例基础辅助类
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/10/12
 */
public abstract class LeetcodeBase {
    protected static final int[][] DIRECTIONS_4 = new int[][]{
        new int[]{-1, 0}, new int[]{0, 1}, new int[]{1, 0}, new int[]{0, -1},
    };

    protected static final int[][] DIRECTIONS_8 = new int[][]{
        new int[]{-1, 0}, new int[]{-1, 1}, new int[]{0, 1}, new int[]{1, 1},
        new int[]{1, 0}, new int[]{1, -1}, new int[]{0, -1}, new int[]{-1, -1},
    };

    protected static List<int[]> getAroundPoint(int row, int col, int rowBound, int colBound, int[][] directions) {
        List<int[]> result = new ArrayList<>(directions.length);
        for (int[] direction : directions) {
            int npRow = row + direction[0];
            int npCol = col + direction[1];
            if (0 <= npRow && npRow < rowBound && 0 <= npCol && npCol < colBound) {
                result.add(new int[]{npRow, npCol});
            }
        }
        return result;
    }

    protected static String prettyArray(Object[] array, String delimiter) {
        return prettyArray(array, delimiter, "", "");
    }

    protected static String prettyArray(Object[] array, String delimiter, String prefix, String suffix) {
        return array == null ? "null" :
            Arrays.stream(array).map(Object::toString).collect(Collectors.joining(delimiter, prefix, suffix));
    }

    protected static String prettyArray(char[] array, String delimiter, String prefix, String suffix) {
        if (array == null) {
            return "";
        }
        int maxIndex = array.length - 1;
        if (maxIndex == -1) {
            return prefix + suffix;
        }
        StringBuilder builder = new StringBuilder(prefix);
        for (int index = 0; ; index++) {
            builder.append(nullToEmpty(array[index]));
            if (index == maxIndex) {
                return builder.append(suffix).toString();
            }
            builder.append(delimiter);
        }
    }

    protected static String prettyMatrix(Object[][] matrix, String delimiter) {
        return prettyMatrix(matrix, delimiter, "", "");
    }

    protected static String prettyMatrix(char[][] matrix, String delimiter) {
        return prettyMatrix(matrix, delimiter, "", "");
    }

    protected static String prettyMatrix(Object[][] matrix, String delimiter, String prefix, String suffix) {
        if (matrix == null || matrix.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Object[] oneRow : matrix) {
            builder.append(prettyArray(oneRow, delimiter, prefix, suffix)).append("\n");
        }
        return builder.toString();
    }

    protected static String prettyMatrix(char[][] matrix, String delimiter, String prefix, String suffix) {
        if (matrix == null || matrix.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (char[] oneRow : matrix) {
            builder.append(prettyArray(oneRow, delimiter, prefix, suffix)).append("\n");
        }
        return builder.toString();
    }

    protected static String[][] toStringMatrix(String value, int row, int col) {
        String[][] matrix = new String[row][col];
        int index = 0;
        for (int rowIndex = 0; rowIndex < row; rowIndex++) {
            for (int colIndex = 0; colIndex < col; colIndex++) {
                matrix[rowIndex][colIndex] = String.valueOf(value.charAt(index++));
            }
        }
        return matrix;
    }

    protected static char[][] toCharMatrix(String value, int row, int col) {
        char[][] matrix = new char[row][col];
        int index = 0;
        for (int rowIndex = 0; rowIndex < row; rowIndex++) {
            for (int colIndex = 0; colIndex < col; colIndex++) {
                matrix[rowIndex][colIndex] = value.charAt(index++);
            }
        }
        return matrix;
    }

    protected static String nullToEmpty(Object value) {
        return value == null ? "" : value.toString();
    }

    protected static int toBitId(int row, int col) {
        return row << 16 | col;
    }

    protected static int getRowFromBitId(int id) {
        return id >>> 16;
    }

    protected static int getColFromBitId(int id) {
        return id & 0xFFFF;
    }

    @AllArgsConstructor
    protected static class TreeNode {
        public int val;

        public TreeNode left;

        public TreeNode right;

        public TreeNode(int val) {
            this(val, null, null);
        }

        @Override
        public String toString() {
            return "(" + val + ":" + nullToEmpty(left) + "," + nullToEmpty(right) + ")";
        }
    }

    @AllArgsConstructor
    protected static class Node {
        public int val;

        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<>();
        }

        public Node(int val) {
            this.val = val;
            neighbors = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "(" + val + ":" + neighbors + ")";
        }
    }
}
