package com.hotring;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-02
 */
public class RingEntryV2 {

    public static final int ADDRESS_BIT_OFFSET = 16; // 地址的偏移量
    public static final int TOTAL_COUNTER_BIT_OFFSET = 48; // totalCounter的偏移量
    public static final int ACTIVE_BIT_OFFSET = 63; // active的偏移量

    public static final long ADDRESS_MASK = 0xFFFFFFFFFFFF0000L; // 地址的掩码
    public static final long TOTAL_COUNTER_MASK = 0x0000FFFFFFFFFF00L; // totalCounter的掩码
    public static final long ACTIVE_MASK = 0x1L; // active的掩码
    // 当前对象地址，通过System.identityHashCode获取，无需实时保存

    private int tag;

    private RingEntry next; // 指向下个哈希表节点，形成链表

    private String key;

    private String val;


    /**
     * 获取active
     */
    public boolean active() {
        // 通过地址最高位判断是否isActive
        return (((long) System.identityHashCode(this) >> ACTIVE_BIT_OFFSET) & ACTIVE_MASK) == 0;
    }

    /**
     * acs 更新
     */
    public void acquireActive() {
        // cas 设置
    }

    /**
     * cas 重置
     */
    public void resetActive() {
        if (!active()) {
            long clearedActive = System.identityHashCode(this) & ~ACTIVE_MASK;
            long resetActive = (long) 0 << ACTIVE_BIT_OFFSET;
        }
    }

    /**
     * 获取totalCounter
     */
    public int totalCounter() {
        return Math.toIntExact(((long) System.identityHashCode(this) >> TOTAL_COUNTER_BIT_OFFSET) & 0xFFL);
    }

    /**
     * cas 重置
     */
    public void resetTotalCounter() {
        long address = System.identityHashCode(this);
    }

    /**
     *  自增
     */
    public void incrTotalCounter() {

    }
}
