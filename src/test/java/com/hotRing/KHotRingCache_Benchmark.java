package com.hotRing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.hotring.KHotRingCache;
import com.hotring.KHotRingCacheV3;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 */
public class KHotRingCache_Benchmark {

    private static final String inputfile = "/Users/zhaozhenhang/project/mq/hotring/src/test/resources/data/test3.data";

    @Test
    public void test() throws Exception {
        BufferedReader input = new BufferedReader(new FileReader(inputfile));
        int M = 1000; // 根据具体情况设置 M 的值
        int N = 10000000;

        KHotRingCacheV3 h = new KHotRingCacheV3(M);

        for (int i = 0; i < M; ++i) {
            String[] values = input.readLine().trim().split("\\s+");
            int key = Integer.parseInt(values[0]);
            int val = Integer.parseInt(values[1]);
            h.insert(key + "", val + "");
        }

        long start = System.currentTimeMillis();
        String line;
        //        int count = 0;
        while ((line = input.readLine()) != null) {
            String key = line.trim();
            h.search(key);
            //            System.out.println("key:  " + key);
            //            System.out.println("count: " + count++);
        }

        long stop = System.currentTimeMillis();

        System.out.println("KHotRingCache:");
        System.out.println("findCnt:" + h.getFindCnt() + "次");
        System.out.println("maxFindCnt:" + h.getMaxFindCnt() + "次");
        System.out.println("minFindCnt:" + h.getMinFindCnt() + "次");
        System.out.println("averageFindCnt:" + (double) h.getFindCnt() / N + "次");
        System.out.println("Use Time:" + (stop - start) / 1000.0 + "s");

        input.close();
    }

    @Test
    public void testCount() throws Exception {
        BufferedReader input = new BufferedReader(new FileReader(inputfile));
        int M = 1000; // 根据具体情况设置 M 的值
        int N = 10000000;

        Map<String, Long> h = new HashMap<>(M);


        long start = System.currentTimeMillis();
        String line;
        //        int count = 0;
        while ((line = input.readLine()) != null) {
            String key = line.trim();
            h.put(key, h.getOrDefault(key, 1L) + 1L);
            //            System.out.println("key:  " + key);
            //            System.out.println("count: " + count++);
        }

        List<Entry<String, Long>> list = new ArrayList<>(h.entrySet());
        list.sort(Entry.comparingByValue());

        for (int i = list.size() - 1; i >= 0 && i > list.size() - 1000; i--) {
            Map.Entry<String, Long> entry = list.get(i);
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }
}
