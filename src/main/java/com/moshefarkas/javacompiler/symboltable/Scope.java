package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

public class Scope<T, K> {
    // all three types of scopes need to be able to recursivly look through it's parents
    public Map<T, K> elements;
    public Scope<T, K> parent;

    @Override 
    public String toString() {
        int parentHashCode = parent != null ? parent.hashCode() : -1;
        return "thisHashCode: " + hashCode() + " parent hashCode: " + parentHashCode + ", elements: " + elements;
    }

    public Scope(Scope<T, K> parent) {
        this.parent = parent;
        elements = new HashMap<>();
    }

    public K getElement(T name) {
        if (elements.containsKey(name)) {
            return elements.get(name);
        }
        if (parent == null)
            return null;
        return parent.getElement(name);
    }

    public boolean hasElement(T name) {
        return getElement(name) != null;
    }

    public void addElement(T name, K element) {
        elements.put(name, element);
    }

    public Scope<T, K> getParent() {
        return this.parent;
    }
}
