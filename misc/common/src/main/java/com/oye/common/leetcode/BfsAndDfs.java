/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oye.common.leetcode;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * BFS&DFS
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/11/4
 */
public class BfsAndDfs extends LeetcodeBase {
    // https://leetcode-cn.com/problems/minesweeper/
    @Test
    public void no_529_minesweeper() {
        assertArrayEquals(toCharMatrix("B1E1BB1M1BB111BBBBBB", 4, 5),
            updateBoard(toCharMatrix("EEEEEEEMEEEEEEEEEEEE", 4, 5), new int[]{3, 0}));
        assertArrayEquals(toCharMatrix("B1E1BB1X1BB111BBBBBB", 4, 5),
            updateBoard(toCharMatrix("B1E1BB1M1BB111BBBBBB", 4, 5), new int[]{1, 2}));
    }

    char[][] updateBoard(char[][] board, int[] click) {
        int rowBound = board.length;
        int colBound = board[0].length;

        if (board[click[0]][click[1]] == 'M') {
            board[click[0]][click[1]] = 'X';
            return board;
        }

        Queue<int[]> queue = new LinkedList<>();

        queue.offer(click);
        while (!queue.isEmpty()) {
            int[] cp = queue.poll();
            char cpSymbol = board[cp[0]][cp[1]];
            if (cpSymbol == 'E') {
                char revealedSymbol = countMines(board, cp);
                board[cp[0]][cp[1]] = revealedSymbol;
                if (revealedSymbol != 'B') {
                    continue;
                }
            } else {
                continue;
            }
            for (int[] np : getAroundPoint(cp[0], cp[1], rowBound, colBound, DIRECTIONS_8)) {
                char npSymbol = board[np[0]][np[1]];
                if (npSymbol == 'E') {
                    queue.offer(np);
                }
            }
        }

        return board;
    }

    private char countMines(char[][] board, int[] point) {
        int rowBound = board.length;
        int colBound = board[0].length;
        int row = point[0];
        int col = point[1];
        int count = 0;
        for (int[] np : getAroundPoint(row, col, rowBound, colBound, DIRECTIONS_8)) {
            if ("MX".indexOf(board[np[0]][np[1]]) != -1) {
                count++;
            }
        }
        return count == 0 ? 'B' : (char) (count + '0');
    }

    // https://leetcode-cn.com/problems/clone-graph/
    @Test
    public void no_133_clone_graph() {
        Node cloneNode = cloneGraph_bfs(new Node(1, newArrayList(new Node(2, newArrayList(new Node(3))), new Node(4))));
        Assertions.assertEquals("(1:[(2:[(3:[])]), (4:[])])", cloneNode.toString());
        Node cloneNode2 =
            cloneGraph_dfs(new Node(1, newArrayList(new Node(2, newArrayList(new Node(3))), new Node(4))));
        Assertions.assertEquals("(1:[(2:[(3:[])]), (4:[])])", cloneNode2.toString());
    }

    Node cloneGraph_dfs(Node node) {
        return cloneGraph_dfs_helper(new HashMap<>(), node);
    }

    /**
     * DFS一般使用递归的方式较为简单，递归不需要限制于dfs(path, choice, result)的形式。
     * <br/>
     * 在遍历的时候，需要将克隆的节点和原节点一一对应，最直接的想法为在遍历原节点的时候保持克隆节点指针同步更新，一一对应可以使用Map的方式。
     * <br/>
     * 考虑DFS遍历，以递归的形式，通过Map保持原节点到克隆节点的索引，定义辅助方法的返回值即为原节点已经克隆完毕的对应节点，
     * 并实时更新映射Map。
     * <br/>
     * 回环的处理：在创建出克隆节点之后需要优先更新到Map中，同时在递归方法中创建克隆节点之前首先检查克隆Map中是否已存在克隆节点，如果存在，
     * 直接返回即可。如此防止回环导致的无限循环（未验证）
     * <br/>
     * 原题目似乎没有检查图的边是否完全克隆
     */
    private Node cloneGraph_dfs_helper(Map<Node, Node> visited, Node node) {
        if (node == null) {
            return null;
        }
        Node cloned = visited.get(node);
        if (cloned == null) {
            cloned = new Node(node.val);
            visited.put(node, cloned); // 优先更新克隆映射Map，避免回环导致的循环
            for (Node neighbor : node.neighbors) {
                cloned.neighbors.add(cloneGraph_dfs_helper(visited, neighbor));
            }
        }
        return cloned;
    }

    /**
     * BFS需要队列。
     * <p>
     * 原节点和克隆节点需要建立映射，映射的目的是可以正确的添加新的克隆节点到对应的目标克隆节点。
     * 放入队列的是按照BFS遍历规则得到的原节点对象；放入Map的key是原节点，value是克隆节点。
     */
    Node cloneGraph_bfs(Node node) {
        if (node == null) {
            return null;
        }

        Map<Node, Node> clonedMap = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();

        Node clonedStartNode = new Node(node.val);
        clonedMap.put(node, clonedStartNode);
        queue.offer(node);

        while (!queue.isEmpty()) {
            Node next = queue.poll();
            for (Node neighbor : next.neighbors) {
                // 由于ensureNeighborCloned方法中已经限制了节点不会重复克隆，因此不存在重复遍历，则不会重复添
                clonedMap.get(next).neighbors.add(ensueNeighborCloned(clonedMap, queue, neighbor));
            }
        }
        return clonedStartNode;
    }

    private Node ensueNeighborCloned(Map<Node, Node> clonedMap, Queue<Node> queue, Node neighbor) {
        Node clonedNeighbor = clonedMap.get(neighbor);
        if (clonedNeighbor == null) {
            clonedNeighbor = new Node(neighbor.val);
            clonedMap.put(neighbor, clonedNeighbor); // 放入map标记已经克隆
            queue.offer(neighbor); // 仅仅需要将尚未clone的放入队列，对于已经克隆的，不需要重复遍历，避免回环循环
        }
        return clonedNeighbor;
    }

    // https://leetcode-cn.com/problems/generate-parentheses/
    @Test
    public void no_22_generate_parentheses() {
        Assertions.assertEquals(Sets.newHashSet("((()))", "(()())", "(())()", "()(())", "()()()"),
            new HashSet<>(generateParenthesis(3)));
        Assertions.assertEquals(Sets.newHashSet("()"), new HashSet<>(generateParenthesis(1)));
    }

    /**
     * 典型的DFS回溯
     * <br/>
     * 回溯的选择只有两个：“(”和“)”。
     * 终止条件：长度达到最大长度
     * 合格条件：长度达到最大长度；括号数量配对；括号顺序合理（右括号不会早于左括号出现）
     * <br/>
     * 使用一个整数值记录括号配对和出现顺序，遇到左括号自增、遇到右括号自减，则对于合格的路径，最终值必须为0，并且每一步值都不小于0（保证
     * 过程中右括号不会早于左括号出现）
     */
    List<String> generateParenthesis(int n) {
        LinkedList<String> result = new LinkedList<>();
        char[] path = new char[n * 2];
        generateParenthesis_dfs(result, path, 0, 0);
        return result;
    }

    private void generateParenthesis_dfs(List<String> result, char[] path, int length, int value) {
        if (value < 0) {
            return;
        }

        if (length >= path.length) {
            if (value == 0) {
                result.add(new String(path));
            }
            return;
        }

        path[length] = '(';
        generateParenthesis_dfs(result, path, length + 1, value + 1);

        path[length] = ')';
        generateParenthesis_dfs(result, path, length + 1, value - 1);
    }

    // https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number/
    @Test
    public void no_17_letter_combinations_of_a_phone_number() {
        Assertions.assertEquals(Sets.newHashSet("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"),
            new HashSet<>(letterCombinations("23")));
        Assertions.assertEquals(Collections.emptySet(),
            new HashSet<>(letterCombinations("")));
        Assertions.assertEquals(Sets.newHashSet("a", "b", "c"),
            new HashSet<>(letterCombinations("2")));
    }

    List<String> letterCombinations(String digits) {
        if (digits.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new LinkedList<>();
        letterCombinationsDfs(new char[digits.length()], result, 0, digits);
        return result;
    }

    private final char[][] digitsLetter = {
        new char[]{'a', 'b', 'c'},
        new char[]{'d', 'e', 'f'},
        new char[]{'g', 'h', 'i'},
        new char[]{'j', 'k', 'l'},
        new char[]{'m', 'n', 'o'},
        new char[]{'p', 'q', 'r', 's'},
        new char[]{'t', 'u', 'v'},
        new char[]{'w', 'x', 'y', 'z'},
    };

    private void letterCombinationsDfs(char[] path, List<String> result, int index, String digits) {
        if (index == path.length) {
            result.add(new String(path));
            return;
        }

        for (char c : digitsLetter[digits.charAt(index) - '0' - 2]) {
            path[index] = c;
            letterCombinationsDfs(path, result, index + 1, digits);
        }
    }

    // https://leetcode-cn.com/problems/validate-binary-search-tree/
    @Test
    public void no_98_validate_binary_search_tree() {
        // TODO 20210720221708
    }

    // boolean isValidBST(TreeNode root) {
    //
    // }

    // https://leetcode-cn.com/problems/restore-ip-addresses/
    @Test
    public void no_93_restore_ip_addresses() {
        Assertions.assertEquals(Arrays.asList("255.255.11.135", "255.255.111.35"), restoreIpAddresses("25525511135"));
        Assertions.assertEquals(Collections.singletonList("0.0.0.0"), restoreIpAddresses("0000"));
        Assertions.assertEquals(Collections.singletonList("1.1.1.1"), restoreIpAddresses("1111"));
        Assertions.assertEquals(Arrays.asList("0.10.0.10", "0.100.1.0"), restoreIpAddresses("010010"));
        Assertions.assertEquals(Arrays.asList("1.0.10.23", "1.0.102.3", "10.1.0.23", "10.10.2.3", "101.0.2.3"),
            restoreIpAddresses("101023"));
    }

    /* DFS + 剪枝 */
    List<String> restoreIpAddresses(String s) {
        List<String> result = new LinkedList<>();
        restoreIpAddresses_dfs(result, new LinkedList<>(), 0, s);
        return result;
    }

    void restoreIpAddresses_dfs(List<String> result, LinkedList<String> parts, int index, String s) {
        if (parts.size() == 4) { // IP段数量已满足
            if (index == s.length()) { // 需要消耗所有的字符
                result.add(String.join(".", parts));
            }
            return;
        }
        if (index >= s.length()) { // 字符不够
            return;
        }
        if (s.charAt(index) == '0') { // 如果以0开头，必须是单个0
            parts.addLast("0");
            restoreIpAddresses_dfs(result, parts, index + 1, s);
            parts.removeLast(); // 撤销选择
            return;
        }
        for (int i = 1; i <= 3; i++) { // 枚举1~3个字符的情况
            if (i + index <= s.length()) {
                String seg = s.substring(index, index + i);
                if (Integer.parseInt(seg) <= 255) {
                    parts.addLast(seg);
                    restoreIpAddresses_dfs(result, parts, index + i, s);
                    parts.removeLast(); // 撤销选择
                }
            } else {
                return; // 一旦字符数量不够，后面的枚举肯定不够
            }
        }
    }

    // https://leetcode-cn.com/problems/the-maze-ii/
    @Test
    public void no_505_the_maze_ii() {

    }

    int shortestDistance(int[][] maze, int[] start, int[] destination) {
        return 0;
    }

    void shortestDistance_dfs(int[] min, int dis, Set<Integer> visited, int[][] maze, int startId,
        int destId) {
        if (startId == destId) {
            min[0] = Math.min(min[0], dis);
            return;
        }
    }

    int[] moveToStop(int[][] maze, int row, int col, int[] direction) {
        int stopRow = row;
        int stopCol = col;
        while (true) {
        }
    }


}
