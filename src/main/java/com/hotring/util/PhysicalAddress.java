package com.hotring.util;

import com.hotring.RingEntry;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-11-02
 * 调用jni获取物理地址
 */
public class PhysicalAddress {

    static {
//        System.loadLibrary("MyDativeDll");
    }

    public static native long callAddress(Object object);

    public static void main(String[] args) {
        RingEntry entry = new RingEntry();
        System.out.println("DLL path: " + System.getProperty("java.library.path"));
        System.out.println(System.identityHashCode(entry));
        System.out.println(System.identityHashCode(entry));
        System.out.println(System.identityHashCode(entry));
//        long address = callAddress(entry);
//        System.out.println(address);
    }
}
