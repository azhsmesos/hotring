package com.hotRing;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;


/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 * 幂律分布
 */
public class SaveRandomData {

    // 偏度因子
    static double theta = 1;

    //总访问数
    static final int N = 10000000;

    // 总项数
    static final int M = 8000;

    //
    static final int MOD = 20000;

    // 频率之和
    static double sum = 0;

    static double lastSum = 0;


    // 大数幂运算
    long largeExponents(long n, long p) {
        long res = 1;
        long base = n;
        while (p != 0) {
            if ((p & 1) != 0) {
                res *= base;
            }
            p = p >> 1;
            base *= base;
        }
        return res;
    }

    // 求频率
    double f(int x) {
        int denominator = 0;
        if (denominator == 0) {
            for (int i = 0; i <= N; i++) {
                denominator += 1.0 / largeExponents(i, (long) theta);
            }
        }
        return (1.0 / largeExponents(x, (long) theta)) / denominator;
    }

    String toStr(int x) {
        StringBuilder res = new StringBuilder();
        while (x != 0) {
            res.append((x % 10) + '0');
            x /= 10;
        }
        return res.reverse().toString();
    }

    String outfile = "my_data1";

    @Test
    public void toSaveFileTest() throws Exception {
        int[] vis = new int[MOD + 50];
        int ed = 99;
        int div = N / 100;
        int key, val;
        int r;
        int fre;

        List<Pair<String, String>> dta = new ArrayList<>();


        List<Integer> frequency = new ArrayList<>();

        // 计算当前量级下，访问分布的频数
        for (int i = 1; i <= N; ++i) {
            double percent = f(i);
            sum += percent;
            if (i % div == 0) {
                fre = (int) ((sum - lastSum) * N);
                // System.out.printf("%3dth=%8d", i / div, fre);
                frequency.add(fre);
                lastSum = sum;
            }
            // if (i % (div * 10) == 0) System.out.println("");
        }

        PrintWriter writer = new PrintWriter(outfile);

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        for (int i = 0; i < M; ++i) {
            while (vis[key = rand.nextInt(MOD)] != 0) {
            } // 去重，得到一个不与其他key值重复的新key值
            vis[key] = 1;
            val = rand.nextInt(MOD);
            dta.add(Pair.of(Integer.toString(key), Integer.toString(val)));
            writer.println(key + " " + val);
        }

        // 模拟访问请求
        while (frequency.get(0) > 0) {
            for (int i = 0; i <= ed; ++i) {
                if (frequency.get(i) > 0) {
                    r = rand.nextInt(10);
                    writer.println(dta.get(i * 10 + r).getKey());
                    frequency.set(i, frequency.get(i) - 1);
                } else {
                    --ed;
                }
            }
        }

        writer.close();
    }
}
