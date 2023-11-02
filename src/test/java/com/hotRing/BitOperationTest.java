package com.hotRing;

import org.junit.Test;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-02
 */
public class BitOperationTest {

    @Test
    public void testBit() {
        System.out.println(Long.toBinaryString(0x1L));
        System.out.println(Long.toBinaryString(0x0000FFFFFFFFFF00L));
        System.out.println(Long.toBinaryString(0xFFL));
        System.out.println(0x7FFF);

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
}
