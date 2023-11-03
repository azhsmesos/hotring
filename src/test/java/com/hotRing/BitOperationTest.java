package com.hotRing;

import org.junit.Test;

import com.hotring.RingEntryV3;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-02
 */
public class BitOperationTest {

    // Constants for bit positions
    private static final int ACTIVE_BIT = 0;
    private static final int REHASH_BIT = 1;
    private static final int OCCUPIED_BIT = 2;
    private static final int TOTAL_COUNTER_START_BIT = 3;
    private static final int TOTAL_COUNTER_END_BIT = 18;
    private static final int COUNTER_START_BIT = 19;
    private static final int COUNTER_END_BIT = 32;

    private int value = 100;

    @Test
    public void testBit() {
        System.out.println(isActive());
        setActive(true);
        System.out.println(isActive());
        setActive(false);
        System.out.println(isActive());
        System.out.println(getCounter());
        setCounter(10);
        System.out.println(getCounter());
        setCounter(20);
        System.out.println(getCounter());
        setCounter(0);
        System.out.println(getCounter());
    }

    private int getBit(int bitPosition) {
        return (value >> bitPosition) & 1;
    }

    private void setBit(int bitPosition, int bitValue) {
        value &= ~(1 << bitPosition); // Clear the bit at the position
        value |= (bitValue << bitPosition); // Set the bit to the given value
    }

    private int getValue(int startBit, int endBit) {
        int mask = ((1 << (endBit - startBit + 1)) - 1) << startBit;
        return (value & mask) >>> startBit;
    }

    private void setValue(int startBit, int endBit, int newValue) {
        int mask = ((1 << (endBit - startBit + 1)) - 1) << startBit;
        value &= ~mask; // Clear the bits in the range
        value |= (newValue << startBit); // Set the bits to the new value
    }

    public boolean isActive() {
        return getBit(ACTIVE_BIT) == 1;
    }

    public void setActive(boolean active) {
        setBit(ACTIVE_BIT, active ? 1 : 0);
    }

    public boolean isRehash() {
        return getBit(REHASH_BIT) == 1;
    }

    public void setRehash(boolean rehash) {
        setBit(REHASH_BIT, rehash ? 1 : 0);
    }

    public boolean isOccupied() {
        return getBit(OCCUPIED_BIT) == 1;
    }

    public void setOccupied(boolean occupied) {
        setBit(OCCUPIED_BIT, occupied ? 1 : 0);
    }

    public int getTotalCounter() {
        return getValue(TOTAL_COUNTER_START_BIT, TOTAL_COUNTER_END_BIT);
    }

    public void setTotalCounter(int counter) {
        setValue(TOTAL_COUNTER_START_BIT, TOTAL_COUNTER_END_BIT, counter);
    }

    public int getCounter() {
        return getValue(COUNTER_START_BIT, COUNTER_END_BIT);
    }

    public void setCounter(int counter) {
        setValue(COUNTER_START_BIT, COUNTER_END_BIT, counter);
    }

    public void incrementTotalCounter() {
        setTotalCounter(getTotalCounter() + 1);
    }

    public void incrementCounter() {
        setCounter(getCounter() + 1);
    }

    public void reset() {
        this.value = 0;
    }
    @Test
    public void testAddress() {
        long value = System.identityHashCode(this);
        short totalCounter = (short) ((value >> 48) & 0xFFFF);
        System.out.println("原始 totalCounter: " + totalCounter);
        System.out.println("原始 value: " + value);

        // 修改totalCounter的值
        int newTotalCounter = 1; // 新的totalCounter值
        value = (value & 0xFFFFFFFFFFFFL) | ((long) newTotalCounter << 48); // 直接在value上修改totalCounter的值

        // 输出修改后的value
        System.out.println("修改后的 value: " + value);

        // 获取修改后的totalCounter的值
        short modifiedTotalCounter = (short) ((value >> 48) & 0xFFFF);
        System.out.println("修改后的 totalCounter: " + modifiedTotalCounter);
    }

    @Test
    public void testEntryV3() {
        RingEntryV3 v3 = new RingEntryV3();
        v3.setMeta(0);
        System.out.println("=======active========");
        System.out.println(v3.active());
        v3.acquireActive();
        System.out.println(v3.active());
        v3.resetActive();
        System.out.println(v3.active());

        System.out.println("=======occupied========");
        System.out.println(v3.occupied());
        v3.acquireOccupied();
        System.out.println(v3.occupied());
        v3.resetOccupied();
        System.out.println(v3.occupied());

        System.out.println("=======rehash========");
        System.out.println(v3.rehash());
        v3.acquireRehash();
        System.out.println(v3.rehash());
        v3.resetRehash();
        System.out.println(v3.rehash());

        System.out.println("=======totalCounter========");
        System.out.println(v3.totalCounter());
        v3.incrTotalCounter(10);
        System.out.println(v3.totalCounter());
        v3.resetTotalCounter();
        System.out.println(v3.totalCounter());

        System.out.println("=======counter========");
        System.out.println(v3.counter());
        v3.incrCounter(10);
        System.out.println(v3.counter());
        v3.incrCounter(20);
        System.out.println(v3.counter());
        for (int i = 0; i < 100; i++) {
            v3.incrCounter(i * 10);
        }
        System.out.println(v3.counter());
    }

    @Test
    public void testMod() {
        System.out.println(-4 % 5);
    }
}
