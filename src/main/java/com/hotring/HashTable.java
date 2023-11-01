package com.hotring;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-10-31
 */
public class HashTable {

    // hash表数组
    private List<RingEntry> table;

    // 哈希表大小
    private int size;

    // hash表大小掩码，用于计算索引值，总是等于 size - 1
    private int sizeMask;

    // 用来记录访问次数r == R时，进行热点转移，R：访问次数
    private int r;

    // 测试常量
    // 控制访问多少次进行热点转移
    private static final int R = 5;

    // 统计总的查找次数
    private int findCnt;

    // 统计最大查找次数，一定程度上可以反应尾延迟
    private int maxFindCnt;

    // 统计最小查找次数
    private int minFindCnt;

    public HashTable(int size) {
        int htsz = 1;
        while (htsz < size) {
            htsz <<= 1;
        }
        table = new ArrayList<>(htsz);
        for (int i = 0; i < htsz; i++) {
            table.add(null);
        }
        this.size = htsz;
        this.sizeMask = htsz - 1;
    }

    public boolean insert(String key, String val) {
        if (search(key) != null) {
            return false;
        }
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;
        int tag = hashVal & (~this.sizeMask);
        RingEntry newEntry = new RingEntry(key, val, null, tag);
        RingEntry pre = null;
        RingEntry next = null;
        newEntry.setNext(this.table.get(index));
        this.table.set(index, newEntry);
        return true;
    }

    public boolean update(String key, String val) {
        RingEntry entry = search(key);
        if (entry == null) {
            return false;
        }
        entry.setVal(val);
        return true;
    }

    public boolean remove(String key) {
        RingEntry entry = search(key);
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;

        if (entry == null) {
            return false;
        }
        if (this.table.get(index) == entry) {
            this.table.set(index, entry.getNext());
        } else {
            RingEntry pre = this.table.get(index);
            while (pre != null && pre.getNext() != entry) {
                pre = pre.getNext();
            }
            if (pre != null) {
                pre.setNext(entry.getNext());
            }
        }
        // Allow garbage collector to reclaim memory
        entry = null;
        return true;
    }

    public RingEntry search(String key) {
        int hashVal = hash(key);
        int index = hashVal & this.sizeMask;
        RingEntry entry = this.table.get(index);
        int preCnt = findCnt;
        boolean hotspotAware = false;
        ++this.findCnt;
        while (entry != null && !entry.getKey().equals(key)) {
            ++this.findCnt;
            entry = entry.getNext();
        }
        setMinMax(this.findCnt - preCnt);
        return entry;
    }

    /**
     * 计算hash值
     */
    public int hash(String key) {
        int hash = 5381;
        for (char c : key.toCharArray()) {
            hash += (hash << 5) + c;
        }
        // return key[0] << 7 将最高位设置为0，非负数
        return (hash & 0x7FFFFFFF);
    }

    /**
     * 设置记录最大查找次数和最小查找次数
     * @param oneCnt 每次查找次数
     */
    public void setMinMax(int oneCnt) {
        this.maxFindCnt = Math.max(this.maxFindCnt, oneCnt);
        this.minFindCnt = Math.min(this.minFindCnt, oneCnt);
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
