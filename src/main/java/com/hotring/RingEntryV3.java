package com.hotring;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-02
 */
public class RingEntryV3 {

    // Constants for bit positions
    private static final int ACTIVE_BIT = 0;
    private static final int REHASH_BIT = 1;
    private static final int OCCUPIED_BIT = 2;
    private static final int TOTAL_COUNTER_START_BIT = 3;
    private static final int TOTAL_COUNTER_END_BIT = 18;
    private static final int COUNTER_START_BIT = 19;
    private static final int COUNTER_END_BIT = 32;

    /**
     * meta第一个bit存储active，第二个bit存储rehash，第三个bit存储occupied
     * 第4-18bit存储totalCounter，第19-32bit存储counter
     * 相比论文实现多处8字节，32bit的内存消耗，但Java只能这么玩了
     */
    private int meta;

    private int tag;

    private RingEntry next; // 指向下个哈希表节点，形成链表

    private String key;

    private String val;

    public RingEntryV3() {
        this.meta = 0;
    }

    public boolean active() {
        return ((meta >> ACTIVE_BIT) & 1) == 0;
    }

    public void acquireActive() {
        meta &= ~(1 << ACTIVE_BIT);
        meta |= (1 << ACTIVE_BIT);
    }

    public void resetActive() {
        meta &= ~(1 << ACTIVE_BIT);
        meta |= (0 << ACTIVE_BIT);
    }

    public boolean rehash() {
        return ((meta >> REHASH_BIT) & 1) == 0;
    }

    public void acquireRehash() {
        meta &= ~(1 << REHASH_BIT);
        meta |= (1 << REHASH_BIT);
    }

    public void resetRehash() {
        meta &= ~(1 << REHASH_BIT);
        meta |= 0;
    }

    public boolean occupied() {
        return ((meta >> OCCUPIED_BIT) & 1) == 0;
    }

    public void acquireOccupied() {
        meta &= ~(1 << OCCUPIED_BIT);
        meta |= (1 << OCCUPIED_BIT);
    }

    public void resetOccupied() {
        meta &= ~(1 << OCCUPIED_BIT);
        meta |= 0;
    }

    public int totalCounter() {
        int mask = ((1 << (TOTAL_COUNTER_END_BIT - TOTAL_COUNTER_START_BIT + 1)) - 1) << TOTAL_COUNTER_START_BIT;
        return (meta & mask) >>> TOTAL_COUNTER_START_BIT;
    }

    public void incrTotalCounter() {
        int mask = ((1 << (TOTAL_COUNTER_END_BIT - TOTAL_COUNTER_START_BIT + 1)) - 1) << TOTAL_COUNTER_START_BIT;
        // clear the bits in the range
        meta &= ~mask;
        // set the bits to the new value
        int totalCounter = totalCounter() + 1;
        meta |= (totalCounter << TOTAL_COUNTER_START_BIT);
    }

    public void resetTotalCounter() {
        int mask = ((1 << (TOTAL_COUNTER_END_BIT - TOTAL_COUNTER_START_BIT + 1)) - 1) << TOTAL_COUNTER_START_BIT;
        meta &= ~mask;
        meta |= 0;
    }

    public int counter() {
        long mask = ((1L << (COUNTER_END_BIT - COUNTER_START_BIT + 1)) - 1) << COUNTER_START_BIT;
        return Math.toIntExact((meta & mask) >>> COUNTER_START_BIT);
    }

    /**
     * mask会溢出
     */
    public void incrCounter() {
        long mask = ((1L << (COUNTER_END_BIT - COUNTER_START_BIT + 1)) - 1) << COUNTER_START_BIT;
        meta &= ~mask;
        int counter = counter() + 1;
        meta |= (counter << COUNTER_START_BIT);
    }

    public void resetCounter() {
        long mask = ((1L << (COUNTER_END_BIT - COUNTER_START_BIT + 1)) - 1) << COUNTER_START_BIT;
        meta &= ~mask;
        meta |= 0;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public RingEntry getNext() {
        return next;
    }

    public void setNext(RingEntry next) {
        this.next = next;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
