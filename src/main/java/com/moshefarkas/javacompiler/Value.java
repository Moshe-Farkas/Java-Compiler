package com.moshefarkas.javacompiler;

public class Value {
    public Object value;
    public Type valueType;

    public Value(Object value, Type valueType) {
        this.value = value;
        this.valueType = valueType;
    }

    public enum Type {
        INT,
        FLOAT,
        CHAR,
        STRING,
        OBJECT,
        BOOL,
        BYTE, 
        SHORT,
        IDENTIFIER,
    }

    @Override 
    public String toString() {
        return value + ", Type: " + valueType + ";";
    }
}
