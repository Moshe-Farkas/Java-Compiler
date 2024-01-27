package com.moshefarkas.javacompiler.dispatching;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class LiteralDispatcherTest {

    @Test
    public void testInt() {
        // iconst_m1 = 2 (0x2)
        byte[] expects = new byte[] {0x2};
        byte[] got = new LiteralDispatcher(-1).dispatch();
        assertArrayEquals(expects, got);

        // iconst_0 = 3 (0x3)
        expects = new byte[] {0x3};
        got = new LiteralDispatcher(0).dispatch();
        assertArrayEquals(expects, got);

        // iconst_1 = 4 (0x4)
        expects = new byte[] {0x4};
        got = new LiteralDispatcher(1).dispatch();
        assertArrayEquals(expects, got);

        // iconst_2 = 5 (0x5)
        expects = new byte[] {0x5};
        got = new LiteralDispatcher(2).dispatch();
        assertArrayEquals(expects, got);

        // iconst_3 = 6 (0x6)
        expects = new byte[] {0x6};
        got = new LiteralDispatcher(3).dispatch();
        assertArrayEquals(expects, got);

        // iconst_4 = 7 (0x7)
        expects = new byte[] {0x7};
        got = new LiteralDispatcher(4).dispatch();
        assertArrayEquals(expects, got);

        // iconst_5 = 8 (0x8)         
        expects = new byte[] {0x8};
        got = new LiteralDispatcher(5).dispatch();
        assertArrayEquals(expects, got);

        // bipush
        // test starting range -> end range
        expects = new byte[] {0x10, 6};
        got = new LiteralDispatcher(6).dispatch();
        assertArrayEquals(expects, got);

        expects = new byte[] {0x10, 127};
        got = new LiteralDispatcher(127).dispatch();
        assertArrayEquals(expects, got);

        // sipush
        expects = new byte[] {0x11, 0x00, (byte)0x80};
        got = new LiteralDispatcher(128).dispatch();
        assertArrayEquals(expects, got);
        
        expects = new byte[] {0x11, (byte)0x7f, (byte)0xff};
        got = new LiteralDispatcher((int)Short.MAX_VALUE).dispatch();
        assertArrayEquals(expects, got);
    }

    @Test
    public void testFloat() {
        // fconst_0 = 11 (0xb)
        byte[] expects = new byte[] {0x0b};
        byte[] got = new LiteralDispatcher(0.0f).dispatch();
        assertArrayEquals(expects, got);

        // fconst_1 = 12 (0xc)
        expects = new byte[] {0x0c};
        got = new LiteralDispatcher(1.0f).dispatch();
        assertArrayEquals(expects, got);

        // fconst_2 = 13 (0xd) 
        expects = new byte[] {0x0d};
        got = new LiteralDispatcher(2.0f).dispatch();
        assertArrayEquals(expects, got);

    }

    @Test 
    public void testChar() {
        byte[] expects = new byte[] {0x11, 0x00, (byte)0x61};
        byte[] got = new LiteralDispatcher('a').dispatch();
        assertArrayEquals(expects, got);

        expects = new byte[] {0x11, 0x00, (byte)0x7d};
        got = new LiteralDispatcher('}').dispatch();
        assertArrayEquals(expects, got);
    }
}
