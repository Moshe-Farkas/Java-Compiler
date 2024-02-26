package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public class ClassManager {
    // singlton that will manage all classes in compilation
    private ClassManager() {
        classes = new HashMap<>();
    }
    private static ClassManager instance;
    
    public static ClassManager getIntsance() {
        if (instance == null)    
            instance = new ClassManager();
        return instance;
    }

    public void test_reset() {
        instance = new ClassManager();
    }

    private Map<String, Clazz> classes;

    public void createNewClass(ClassNode node) {
        classes.put(node.className, new Clazz(node));
    }

    public boolean hasClass(String className) {
        return classes.containsKey(className);
    }

    public Clazz getClass(String className) {
        if (!hasClass(className))
            return null;
        return classes.get(className);
    }
}
