package com.moshefarkas.javacompiler.dispatching;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.Value.Type;

public class StoreDispatcherTest {

    // need to make a mock symbole table
    // private void compileSymbolTable(VarInfo... vars) {
    //     SymbolTable.getInstance().test_reset();
    //     for (VarInfo var : vars) {
    //         SymbolTable.getInstance().addLocal(var.name, var);
    //     }
    // }

    @Test
    public void testInt() {
        VarInfo v0 = new VarInfo();
        v0.localIndex = 0;
        v0.name = "v0";
        v0.type = Type.INT;
        VarInfo v1 = new VarInfo();
        v1.localIndex = 1;
        v1.name = "v1";
        v1.type = Type.INT;
        VarInfo v2 = new VarInfo();
        v2.localIndex = 2;
        v2.name = "v2";
        v2.type = Type.INT;
        VarInfo v3 = new VarInfo();
        v3.localIndex = 3;
        v3.name = "v3";
        v3.type = Type.INT;
        VarInfo v4 = new VarInfo();
        v4.localIndex = 4;
        v4.name = "v4";
        v4.type = Type.INT;

        // istore_0 = 59 (0x3b)
        byte[] expected = new byte[] {0x3b};
        byte[] got = new StoreDispatcher(v0).dispatch();
        assertArrayEquals(expected, got);

        // istore_1 = 60 (0x3c)
        expected = new byte[] {0x3c};
        got = new StoreDispatcher(v1).dispatch();
        assertArrayEquals(expected, got);

        // istore_2 = 61 (0x3d)
        expected = new byte[] {0x3d};
        got = new StoreDispatcher(v2).dispatch();
        assertArrayEquals(expected, got);

        // istore_3 = 62 (0x3e) 
        expected = new byte[] {0x3e};
        got = new StoreDispatcher(v3).dispatch();
        assertArrayEquals(expected, got);

        // istore index
        expected = new byte[] {0x36, 4};
        got = new StoreDispatcher(v4).dispatch();
        assertArrayEquals(expected, got);
    }
}
