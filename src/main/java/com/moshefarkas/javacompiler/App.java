package com.moshefarkas.javacompiler;

public class App {
    public static void main( String[] args ) throws Exception {
        for (String file : args) {
            if (!file.endsWith(".java")) {
                System.out.printf("Can't compile file `%s`. File needs to be a java source file.\n", file);
                System.exit(1);
            }
        }
        Compiler.compileFiles(args);
        System.out.println("\n--DONE--\n");
    }
}
