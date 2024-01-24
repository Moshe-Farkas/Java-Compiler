
package src.com.jc;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HW {

    public static void main(String[] args) {
        try {
            DataOutputStream dos = new DataOutputStream(
                                new FileOutputStream("HW.class"));
            

            // magic
            dos.writeInt(0xcafebabe);

            // major and minor
            dos.writeShort(0); 
            dos.writeShort(55); 

            // constant pool
            emitCP(dos);

            // access flags
            dos.writeShort(33);

            // this_class
            dos.writeShort(2);  // class info index for main

            // super_class 
            dos.writeShort(3);  // java.lang.object

            // interface count
            dos.writeShort(0);

            // fields count
            dos.writeShort(0);


            emitMethods(dos);

            // attr count
            dos.writeShort(1);

            emitClassAttr(dos);


            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("DONE");
    }

    private static void emitCP(DataOutputStream dos) throws IOException {
        // const count
        dos.writeShort(15); 

        // #1 = Methodref          #3.#12         // java/lang/Object."<init>":()V
        dos.writeByte(10);  // tag
        dos.writeShort(3); // class index
        dos.writeShort(12); // name and type index


        // #2 = Class              #13            // Main
        dos.writeByte(7);   // tag
        dos.writeShort(13); // name index
        

        // #3 = Class              #14            // java/lang/Object
        dos.writeByte(7);   // tag
        dos.writeShort(14); // name index

        // #4 = Utf8               <init>
        emitUTF8(dos, "<init>");

        // #5 = Utf8               ()V
        emitUTF8(dos, "()V");
        
        // #6 = Utf8               Code
        emitUTF8(dos, "Code");

        // #7 = Utf8               LineNumberTable
        emitUTF8(dos, "LineNumberTable");; 


        // #8 = Utf8               main
        emitUTF8(dos, "main");


        // #9 = Utf8               ([Ljava/lang/String;)V
        emitUTF8(dos, "([Ljava/lang/String;)V");


        // #10 = Utf8               SourceFile
        emitUTF8(dos, "SourceFile");

        // #11 = Utf8               Main.java
        emitUTF8(dos, "Main.java");

        // #12 = NameAndType        #4:#5          // "<init>":()V
        dos.writeByte(12);
        dos.writeShort(4);
        dos.writeShort(5);

        // #13 = Utf8               Main
        emitUTF8(dos, "Main");

        // #14 = Utf8               java/lang/Object
        emitUTF8(dos, "java/lang/Object");


        // #15 = methodRef println
        dos.writeByte(10); 
        dos.writeShort(16); // class index 
        dos.writeShort(18); // name and type index


        // #16 = class
        dos.writeByte(7);
        dos.writeShort(2); // name index

        // #17
        emitUTF8(dos, "java/io/PrintStream");

        // #18
        dos.writeByte(12); // name and type
        dos.writeShort(19); // name index
        dos.writeShort(20); // descriptor index
        
        // #19
        emitUTF8(dos, "println");

        // #20 
        emitUTF8(dos, "(Ljava/lang/String;)V");

        // #21 
        // hello, world string
        dos.writeByte(8);
        dos.writeShort(22); // ut8 hello, world

        // #22
        emitUTF8(dos, "hello, world");

        // #23 
        // field ref printStream
        dos.writeByte(9);
        dos.writeShort(24); // class index 
        dos.writeShort(26); // name and type index

        // #24
        // class java/lang/System
        dos.writeByte(7); // class tag
        dos.writeShort(25); // name index 

        // #25
        emitUTF8(dos, "java/lang/System");


        // #26
        dos.writeByte(12);
        dos.writeShort(27); // name index 
        dos.writeShort(28); // descriptor index


        // #27
        emitUTF8(dos, "out");

        // #28
        emitUTF8(dos, "Ljava/io/PrintStream;");
    }

    private static void emitUTF8(DataOutputStream dos, String string) throws IOException {
        dos.writeByte(1);       // tag
        dos.writeShort(string.length());      // bytes length
        dos.writeBytes(string);
    }

    private static void emitMethods(DataOutputStream dos) throws IOException {
        // methods count
        dos.writeShort(2);

        // <init>
            // access flags
            dos.writeShort(1);

            // name index <init> 
            dos.writeShort(4);

            // method descriptor ()V
            dos.writeShort(5);

            // attr count
            dos.writeShort(1);

            emitCodeAttrInit(dos);
        

        // main method
            // access flags
            dos.writeShort(9); // ACC_PUBLIC, ACC_STATIC

            // name index main
            dos.writeShort(8);

            // method descriptor ()V
            dos.writeShort(9);

            // attr count
            dos.writeShort(1);

            emitCodeAttrMain(dos);
    }

    private static void emitCodeAttrMain(DataOutputStream dos) throws IOException {
        
        // attr name index (Code)
        dos.writeShort(6);
        
        // // attr length
        dos.writeInt(21); 

        // max stack
        dos.writeShort(2);
        
        // max locals
        dos.writeShort(1);

        // code length
        dos.writeInt(12);

        // code:
            dos.writeByte(0xb2);
            dos.writeShort(23); // index for fieldRef PrintStream

            dos.writeByte(12);
            dos.writeByte(21); // index for hello, world

            dos.writeByte(0xb6);
            dos.writeShort(15); // index for methodRef for println

            dos.writeByte(0xb1);

        // exception table length
        dos.writeShort(0);

        // attr count
        dos.writeShort(0);
    }

    private static void emitCodeAttrInit(DataOutputStream dos) throws IOException {
        // attr name index (Code)
        dos.writeShort(6);

        // attr length
        dos.writeInt(29); 

        // max stack
        dos.writeShort(1);
        
        // max locals
        dos.writeShort(1);

        // code length
        dos.writeInt(5);
        
        // code:
            //  0: aload_0
            dos.writeByte(0x2a);

            //  1: invokespecial #1                  // Method java/lang/Object."<init>":()V
            dos.writeByte(0xb7); 
            dos.writeShort(1);

            //  4: return
            dos.writeByte(0xb1);

        // expection table length
        dos.writeShort(0);

        // attr count 
        dos.writeShort(1);
        
        emitLineNumberTable(dos);
        
    }
    private static void emitLineNumberTable(DataOutputStream dos) throws IOException {
        // attr name index (LineNumberTable)
        dos.writeShort(7);

        // attr length
        dos.writeInt(6);

        // line_number_table_length
        dos.writeShort(1);

        // start_pc
        dos.writeShort(0);
        
        // line_number
        dos.writeShort(1);
    }

    private static void emitClassAttr(DataOutputStream dos) throws IOException {
        // attr name index (Main.java)
        dos.writeShort(10);

        // attr length
        dos.writeInt(2);

        // source file string index 
        dos.writeShort(11);
    }
}