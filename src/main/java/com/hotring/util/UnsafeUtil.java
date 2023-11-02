package com.hotring.util;

import java.lang.reflect.Field;
import java.util.Arrays;

import com.hotring.RingEntryV2;

import sun.misc.Unsafe;


/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-02
 */
public class UnsafeUtil {

    private static final long POINTER_SIZE = Unsafe.ARRAY_OBJECT_INDEX_SCALE;

    private static Unsafe unsafe;

    private RingEntryV2 entryV2;

    static {
        try {
            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            unsafe = (Unsafe) theUnsafeField.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 很 hack，有gc的存在，即便你改了指针，因为gc也会导致指针变化，无法控制
     * 。。。。。。。 貌似只能新增属性了，那这样子就无法有效利用空间了...
     */
    public void setActive() {
        long headAddress = unsafe.objectFieldOffset(RingEntryV2.class.getDeclaredFields()[1]);
        long ptr = headAddress | (1L << 63);
        unsafe.putLong(this, headAddress, ptr);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(RingEntryV2.class.getDeclaredFields()));
    }
}
