// package com.moshefarkas.javacompiler.codegen;


// import org.junit.Test;
// import org.objectweb.asm.Opcodes;

// public class MethodGenVisitorTest extends BaseCodeGenTest {

//     @Test
//     public void testTypeIns() {

//         compile("float a = 4;");
//         compareOps(byteArray(Opcodes.I2F));

//         compile("int a = 4 + 8;");
//         compareOps(byteArray(Opcodes.IADD));

//         compile("int a = 4 - 3;");
//         compareOps(byteArray(Opcodes.ISUB));

//         // float
//         compile("float a = 4f + 5;");
//         compareOps(byteArray(Opcodes.I2F, Opcodes.FADD));

//         compile("float a = 4f - 5;");
//         compareOps(byteArray(Opcodes.I2F, Opcodes.FSUB));

//         compile("float a = 4 - 5;");
//         compareOps(byteArray(Opcodes.ISUB, Opcodes.I2F));

//         compile("float a = 4 + 4f - 9;");
//         compareOps(byteArray(
//             Opcodes.I2F,
//             Opcodes.FADD,
//             Opcodes.I2F,
//             Opcodes.FSUB
//         ));

//         compile("float a = 3 - 9 / 45f;");
//         compareOps(byteArray(
//             Opcodes.I2F,
//             Opcodes.I2F,
//             Opcodes.FDIV,
//             Opcodes.FSUB
//         ));

//         compile("int a = 4; byte b = 5; int c = a - b;");
//         compareOps(byteArray(
//             Opcodes.ISUB
//         ));

//         compile("int a = 3; float b = 4/a;");
//         compareOps(byteArray(
//             Opcodes.IDIV,
//             Opcodes.I2F
//         ));
//     }
// }
