package com.moshefarkas.javacompiler.codegen;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConstantPool {

    private static ConstantPool instance;
    private ConstantPool() {}
    public static ConstantPool getInstance() {
        if (instance == null)
            instance = new ConstantPool();
        return instance;
    }

    private final int CLASS = 7;
    private final int INTEGER = 3;


    public void debugPrintPool() {
        System.out.println("--------------------------");
        System.out.println("\tCONSTANT POOL:");
        for (Map.Entry<Object, CpEntry> entry : pool.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("--------------------------");
    }

    private Map<Object, CpEntry> pool = new HashMap<>();
    private byte index = 1;

    public int getCount() {
        return pool.size() + 1;     // always +1 for some reason
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        List<Map.Entry<Object, CpEntry>> orderedPool = new ArrayList<>(pool.entrySet());
        Collections.sort(orderedPool, new CpEntryComparator());
        int index = 1;
        for (Map.Entry<Object, CpEntry> entry : orderedPool) {
            // tag
            dos.writeByte(entry.getValue().tag);
        }
        dos.writeInt(0xcafebabe);

        return bos.toByteArray();
    }

    // int entry -> tag, data

    public byte insertInteger(int value) {
        if (!pool.containsKey(value)) {
            pool.put(value, new CpEntry(INTEGER, index++));
        }
        return pool.get(value).index;
    }

    public byte insertClass(String className) {
        if (!pool.containsKey(className)) {
            pool.put(className, new CpEntry(CLASS, index++));
        }
        return pool.get(className).index;
    }

    private static class CpEntry {

        public int tag;
        public byte index;

        public CpEntry(int tag, byte index) {
            this.tag = tag;
            this.index = index;
        }

        @Override 
        public String toString() {
            return ""+tag;
        }
    }
    private static class CpEntryComparator implements Comparator<Map.Entry<Object, CpEntry>> {

        @Override
        public int compare(Entry<Object, CpEntry> arg0, Entry<Object, CpEntry> arg1) {
            return Integer.compare(arg0.getValue().index, arg1.getValue().index);
        }
    }
}

