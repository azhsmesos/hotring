package com.hotring;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-01
 */
public class HotRing {

    // hash表数组
    private List<RingEntry> table;

    // 哈希表大小
    private int size;

    // hash表大小掩码，用于计算索引值，总是等于 size - 1
    private int sizeMask;

    // 用来记录访问次数r == R时，进行热点转移，R：访问次数
    private int r;

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

    public HotRing(int size) {
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
    }

    public List<RingEntry> getTable() {
        return table;
    }

    public void setTable(List<RingEntry> table) {
        this.table = table;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSizeMask() {
        return sizeMask;
    }

    public void setSizeMask(int sizeMask) {
        this.sizeMask = sizeMask;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public RingEntry getCompareItem() {
        return compareItem;
    }

    public void setCompareItem(RingEntry compareItem) {
        this.compareItem = compareItem;
    }

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
