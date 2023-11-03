package com.hotring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-03
 */
public class KHotRingCacheV3 implements HotRingCache<String, String, RingEntryV3> {

    // hash表数组
    private List<RingEntryV3> table;

    // 哈希表大小
    private int size;

    // hash表大小掩码，用于计算索引值，总是等于 size - 1
    private int sizeMask;

    // 用来记录访问次数r == R时，进行热点转移，R：访问次数
    private ThreadLocal<AtomicInteger> r;

    // 查询时比较
    private RingEntryV3 compareItem;

    // 测试常量
    // 控制访问多少次进行热点转移
    private static final int R = 5;

    // 统计总的查找次数
    private int findCnt;

    // 统计最大查找次数，一定程度上可以反应尾延迟
    private int maxFindCnt;

    // 统计最小查找次数
    private int minFindCnt;

    public KHotRingCacheV3(int size) {
        this.findCnt = 0;
        this.minFindCnt = Integer.MAX_VALUE;
        this.maxFindCnt = 0;

        int htsz = 1;
        while (htsz < size) {
            htsz <<= 1;
        }
        this.table = new ArrayList<>(htsz);
        for (int i = 0; i < htsz; i++) {
            table.add(null);
        }
        this.size = htsz;
        this.sizeMask = htsz - 1;
        this.compareItem = new RingEntryV3("", 0);
        AtomicInteger n = new AtomicInteger(0);
        this.r = new ThreadLocal<>();
        this.r.set(n);
    }

    @Override
    public RingEntryV3 search(String key) {
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;
        int tag = hashVal & (~this.sizeMask);
        RingEntryV3 pre = null;
        RingEntryV3 next = null;
        RingEntryV3 headItem = this.table.get(index);
        int preCnt = findCnt;
        this.compareItem.setKey(key);
        this.compareItem.setTag(tag);


        this.r.get().incrementAndGet();
        if (headItem == null) { // 环中0项
            return null;
        }
        headItem.incrTotalCounter(headItem.totalCounter() + 1);
        ++this.findCnt;

        if (headItem.getNext() == headItem) { // 环中一项
            if (StringUtils.equals(headItem.getKey(), key)) {
                if (r.get().intValue() >= R) {
                    // 只有一项，无需采样
                    r.get().set(0);
                }
                return headItem;
            }
        } else { // 环中n项
            pre = headItem;
            if (!headItem.active()) {
                // 采样
                pre.incrCounter(pre.counter() + 1);
            }
            next = headItem.getNext();
            for (; ; ) {
                if (compareItem.compareTo(pre) == 0) {
                    if (this.r.get().intValue() >= R && pre.compareTo(headItem) != 0) {
                        // 非热点，开启采样
                        if (!headItem.active()) {
                            // 计算频率 热点偏移
                            hotspotMoveItem(index, headItem.totalCounter());
                            headItem.resetActive();
                            headItem.resetCounter();
                        } else {
                            headItem.acquireActive();
                        }
                        this.r.get().set(0);
                    }
                    return pre;
                }
                int compare1 = pre.compareTo(compareItem);
                int compare2 = compareItem.compareTo(next);
                int compare3 = next.compareTo(pre);

                if ((compare1 < 0 && compare2 < 0) || (compare2 < 0 & compare3 < 0) || (compare3 < 0 && compare1 < 0)) {
                    return null;
                }
                next = next.getNext();
                pre = pre.getNext();
                ++this.findCnt;
            }
        }
        setMinMax(this.findCnt - preCnt);
        return null;
    }

    @Override
    public boolean insert(String key, String val) {
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;
        int tag = hashVal & (~this.sizeMask);

        RingEntryV3 newItem = new RingEntryV3(key, val, null, tag);
        RingEntryV3 pre = null;
        RingEntryV3 next = null;
        RingEntryV3 headItem = this.table.get(index);

        if (headItem == null) { // 环中0项
            this.table.set(index, newItem);
            newItem.setNext(newItem);
        } else if (headItem.getNext() == headItem) {
            headItem.setNext(newItem);
            newItem.setNext(headItem);
        } else { // 环中多项
            pre = headItem;
            next = headItem.getNext();
            for (; ; ) {
                int compare1 = pre.compareTo(newItem);
                int compare2 = newItem.compareTo(next);
                int compare3 = next.compareTo(pre);

                if ((compare1 < 0 && compare2 < 0) || (compare2 < 0 & compare3 < 0) || (compare3 < 0 && compare1 < 0)) {
                    newItem.setNext(next);
                    pre.setNext(newItem);
                    break;
                }
                next = next.getNext();
                pre = pre.getNext();
            }
        }
        return true;
    }

    @Override
    public boolean update(String key, String val) {
        // RCU更新
        RingEntryV3 entry = search(key);
        if (entry == null) {
            return false;
        }

        entry.setVal(val);
        return true;
    }

    @Override
    public boolean delete(String key) {
        return false;
    }

    /**
     * 设置记录最大查找次数和最小查找次数
     *
     * @param oneCnt 每次查找次数
     */
    private void setMinMax(int oneCnt) {
        this.maxFindCnt = Math.max(this.maxFindCnt, oneCnt);
        this.minFindCnt = Math.min(this.minFindCnt, oneCnt);
    }

    /**
     * 计算权重，热点偏移
     */
    private void hotspotMoveItem(int index, int counter) {
        RingEntryV3 headItem = this.table.get(index);
        if (headItem == null) { // 理论上不会有，防御性编程，后续去掉
            return;
        }
        RingEntryV3 next = headItem.getNext();
        RingEntryV3 newHeadItem = null;
        double maxVal = Double.MAX_VALUE;
        // 保证不匹配遍历整个环，不过得确认下极端情况是否会存在这个值
        RingEntryV3 compareItem = new RingEntryV3("", "", null, Integer.MAX_VALUE);
        if (headItem == next) { // 只有一项，直接返回
            return;
        }
        for (; ; ) {
            double weight = calculation.apply(headItem, counter);
            headItem.resetCounter();
            if (weight < maxVal) {
                newHeadItem = headItem;
                maxVal = weight;
            }

            int compare1 = headItem.compareTo(compareItem);
            int compare2 = compareItem.compareTo(next);
            int compare3 = next.compareTo(headItem);

            if ((compare1 < 0 && compare2 < 0) || (compare2 < 0 & compare3 < 0) || (compare3 < 0 && compare1 < 0)) {
                break;
            }
            next = next.getNext();
            headItem = headItem.getNext();
        }
        this.table.set(index, newHeadItem);
    }

    BiFunction<RingEntryV3, Integer, Double> calculation = (item, totalCounter) -> {
        // todo 计算权重公式 太复杂先简单这么做
        return (double) item.counter() / totalCounter;
    };

    public int getFindCnt() {
        return findCnt;
    }

    public void setFindCnt(int findCnt) {
        this.findCnt = findCnt;
    }

    public int getMaxFindCnt() {
        return maxFindCnt;
    }

    public void setMaxFindCnt(int maxFindCnt) {
        this.maxFindCnt = maxFindCnt;
    }

    public int getMinFindCnt() {
        return minFindCnt;
    }

    public void setMinFindCnt(int minFindCnt) {
        this.minFindCnt = minFindCnt;
    }
}
