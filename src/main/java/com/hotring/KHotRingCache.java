package com.hotring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 */
public class KHotRingCache implements HotRingCache<String, String, RingEntry> {

    // hash表数组
    private List<RingEntry> table;

    // 哈希表大小
    private int size;

    // hash表大小掩码，用于计算索引值，总是等于 size - 1
    private int sizeMask;

    // 用来记录访问次数r == R时，进行热点转移，R：访问次数
    private ThreadLocal<AtomicInteger> r;

    // 查询时比较
    private RingEntry compareItem;

    // 测试常量
    // 控制访问多少次进行热点转移
    private static final int R = 5;

    // 统计总的查找次数
    private int findCnt;

    // 统计最大查找次数，一定程度上可以反应尾延迟
    private int maxFindCnt;

    // 统计最小查找次数
    private int minFindCnt;

    public KHotRingCache(int size) {
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
        this.compareItem = new RingEntry("", 0);
        AtomicInteger n = new AtomicInteger(0);
        this.r = new ThreadLocal<>();
        this.r.set(n);
    }

    @Override
    public RingEntry search(String key) {
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;
        int tag = hashVal & (~this.sizeMask);
        RingEntry pre = null;
        RingEntry next = null;
        RingEntry  res = null;
        int preCnt = findCnt;
        boolean hostSpotAware = false;
        this.compareItem.setKey(key);
        this.compareItem.setTag(tag);

        this.r.get().incrementAndGet();
        if (this.r.get().intValue() == R) {
            hostSpotAware = true;
            r.get().set(0);
        }
        ++this.findCnt;
        if (this.table.get(index) == null) { // 环中0项
            res = null;
        } else if (this.table.get(index).getNext() == this.table.get(index)) { // 环中一项
            if (StringUtils.equals(this.table.get(index).getKey(), key)) {
                res = this.table.get(index);
            }
        } else { // 环中n项
            pre = this.table.get(index);
            next = this.table.get(index).getNext();
            for (; ; ) {
                if (StringUtils.equals(pre.getKey(), key)) {
                    if (hostSpotAware) {
                        this.table.set(index, pre);
                    }
                    res = pre;
                    break;
                }
                int compare1 = pre.compareTo(compareItem);
                int compare2 = compareItem.compareTo(next);
                int compare3 = next.compareTo(pre);

                if ((compare1 < 0 && compare2 < 0) || (compare2 < 0 & compare3 < 0) || (compare3 < 0 && compare1 < 0)) {
                    res = null;
                    break;
                }
                next = next.getNext();
                pre = pre.getNext();
                ++this.findCnt;
            }
        }
        setMinMax(this.findCnt - preCnt);
        return res;
    }

    @Override
    public boolean insert(String key, String val) {
        // todo 优化项，封装一个内部方法，返回entry时同时返回这个entry的位置，不然每次都得调用多次
        if (search(key) != null) {
            return false;
        }
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;
        int tag = hashVal & (~this.sizeMask);

        RingEntry newItem = new RingEntry(key, val, null, tag);
        RingEntry pre = null;
        RingEntry next = null;

        if (this.table.get(index) == null) { // 环中0项
            this.table.set(index, newItem);
            newItem.setNext(newItem);
        } else if (this.table.get(index).getNext() == this.table.get(index)) {
            if (newItem.compareTo(this.table.get(index)) < 0) {
                newItem.setNext(this.table.get(index));
                this.table.get(index).setNext(newItem);
            } else {
                newItem.setNext(this.table.get(index));
                this.table.get(index).setNext(newItem);
            }
        } else { // 环中多项
            pre = this.table.get(index);
            next = this.table.get(index).getNext();
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
        RingEntry entry = search(key);
        if (entry == null) {
            return false;
        }
        entry.setVal(val);
        return true;
    }

    @Override
    public boolean delete(String key) {
        RingEntry entry = search(key);
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;

        if (entry == null) {
            return false;
        }

        RingEntry pre = entry;
        while (pre.getNext() != entry) {
            pre = pre.getNext();
        }
        pre.setNext(entry.getNext());

        // 头指针指向的节点被删除
        if (this.table.get(index) == entry) {
            // 说明只有一项
            if (pre == entry) {
                this.table.set(index, null);
            } else {
                this.table.set(index, entry.getNext());
            }
        }
        // Allow garbage collector to reclaim memory
        entry = null;
        return true;
    }

    /**
     * 设置记录最大查找次数和最小查找次数
     * @param oneCnt 每次查找次数
     */
    public void setMinMax(int oneCnt) {
        this.maxFindCnt = Math.max(this.maxFindCnt, oneCnt);
        this.minFindCnt = Math.min(this.minFindCnt, oneCnt);
    }

    public int getFindCnt() {
        return findCnt;
    }

    public int getMaxFindCnt() {
        return maxFindCnt;
    }

    public int getMinFindCnt() {
        return minFindCnt;
    }
}
