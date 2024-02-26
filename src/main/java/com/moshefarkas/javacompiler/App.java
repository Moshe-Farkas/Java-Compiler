package com.moshefarkas.javacompiler;

public class App {

    public static void main( String[] args ) throws Exception {
        // class A {
        //     int aField;
        // }

        // class B extends A {
        //     A bField;
        // }

        // Scope<String, VarInfo> aFields = new Scope<String, VarInfo>(null);
        // VarInfo aField = new VarInfo();
        // aField.name = "aField";
        // aFields.addElement("aField", aField);
        // // ------------------------------------------------------------------------------------------------
        // Scope<String, VarInfo> bFields = new Scope<String, VarInfo>(null);
        // VarInfo bField = new VarInfo();
        // bField.name = "bField";
        // bFields.addElement("bField", bField);
        // // ------------------------------------------------------------------------------------------------
        // bFields.parent = aFields;

        // System.out.println(bFields.hasElement("aField"));
        // System.out.println(bFields.hasElement("bField"));

        // System.out.println();
        // System.out.println(aFields.hasElement("aField"));
        // System.out.println(aFields.hasElement("bField"));
        
        
        // System.exit(0);
        

        // String inputFile = null;
        // if ( args.length>0 ) inputFile = args[0];
        // InputStream is = System.in;
        // if ( inputFile!=null ) is = new FileInputStream(inputFile);
        // CharStream input = CharStreams.fromStream(is);
        // Java8Lexer lexer = new Java8Lexer(input);
        // CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Java8Parser parser = new Java8Parser(tokens);
        // ParseTree tree = parser.compilationUnit(); 
        // if (parser.getNumberOfSyntaxErrors() > 0) {
        //     return;
        // }

        // // this is the ast
        // ClassVisitor visitor = new ClassVisitor();
        // visitor.visit(tree);
        
        // ClassNode ast = visitor.currentClass;
        // System.out.println("-------------------------------------------");


        // // AstPrintVisitor oktemp = new AstPrintVisitor();
        // // oktemp.visitClassNode(ast);


        // // semantic analysis
        // new SemanticAnalysis(ast);
        // if (SemanticAnalysis.hadErr) {
        //     return;
        // }

        // // end semantic analysis
        // System.out.println("-------------------------------------------");
        // AstPrintVisitor printVisitor = new AstPrintVisitor();
        // printVisitor.visitClassNode(ast);
        // System.out.println("-------------------------------------------");
        // MethodManager.getInstance().debug_print_methods();
        // System.out.println("-------------------------------------------");
        // // code gen
        // ClassGenVisitor classGen = new ClassGenVisitor();
        // classGen.visitClassNode(ast);

        // FileOutputStream fos = new FileOutputStream("Demo.class");
        // fos.write(classGen.classWriter.toByteArray());
        // fos.close();

        
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
