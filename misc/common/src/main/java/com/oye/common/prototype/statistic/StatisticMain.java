package com.oye.common.prototype.statistic;

/**
 * StatisticMain
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-10-20
 */
public class StatisticMain {
    public static void main(String[] args) {
        StatisticTable maxTable = new StatisticTable(StatisticTable.StatisticMethod.MAX);
        StatisticTable minTable = new StatisticTable(StatisticTable.StatisticMethod.MIN);
        StatisticTable sumTable = new StatisticTable(StatisticTable.StatisticMethod.SUM);
        StatisticTable countTable = new StatisticTable(StatisticTable.StatisticMethod.COUNT);
        StatisticTable averageTable = new StatisticTable(StatisticTable.StatisticMethod.AVERAGE);
        final int max = 2;
        for (int i = 0; i < 10; i++) {
            for (int x = 0; x < max; x++) {
                for (int y = 0; y < max; y++) {
                    for (int z = 0; z < max; z++) {
                        long value = (long) (Math.random() * 100);
                        maxTable.statistic(value, x, y, z);
                        minTable.statistic(value, x, y, z);
                        sumTable.statistic(value, x, y, z);
                        countTable.statistic(value, x, y, z);
                        averageTable.statistic(value, x, y, z);
                    }
                }
            }
        }
        System.out.println(maxTable.getStatisticMethod());
        maxTable.getResult().stream().map(StatisticTable.StatisticItem::toString).forEach(System.out::println);
        System.out.println();
        System.out.println(minTable.getStatisticMethod());
        minTable.getResult().stream().map(StatisticTable.StatisticItem::toString).forEach(System.out::println);
        System.out.println();
        System.out.println(sumTable.getStatisticMethod());
        sumTable.getResult().stream().map(StatisticTable.StatisticItem::toString).forEach(System.out::println);
        System.out.println();
        System.out.println(countTable.getStatisticMethod());
        countTable.getResult().stream().map(StatisticTable.StatisticItem::toString).forEach(System.out::println);
        System.out.println();
        System.out.println(averageTable.getStatisticMethod());
        averageTable.getResult().stream().map(StatisticTable.StatisticItem::toString).forEach(System.out::println);
        System.out.println();
    }
}
