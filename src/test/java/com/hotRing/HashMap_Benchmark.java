package com.hotRing;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Test;

import com.hotring.HotHashMap;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 */
public class HashMap_Benchmark {

    private static final String inputfile = "/Users/zhaozhenhang/project/mq/hotring/src/test/resources/data/test3.data";

    @Test
    public void testHashMap() throws Exception {
        BufferedReader input = new BufferedReader(new FileReader(inputfile));
        int M = 1000; // 根据具体情况设置 M 的值
        int N = 10000000;

        HotHashMap h = new HotHashMap(M);

        for (int i = 0; i < M; ++i) {
            String[] values = input.readLine().trim().split("\\s+");
            int key = Integer.parseInt(values[0]);
            int val = Integer.parseInt(values[1]);
            h.put(key + "", val + "");
        }

        long start = System.currentTimeMillis();
        String line;
        int count = 0;
        while ((line = input.readLine()) != null) {
            String key = line.trim();
            h.get(key);
            //            System.out.println("key:  " + key);
            //            System.out.println("count: " + count++);
        }

        long stop = System.currentTimeMillis();

        System.out.println("HashMap:");
        System.out.println("findCnt:" + h.getFindCnt() + "次");
        System.out.println("averageFindCnt:" + (double) h.getFindCnt() / N + "次");
        System.out.println("Use Time:" + (stop - start) / 1000.0 + "s");

        input.close();
    }
}
