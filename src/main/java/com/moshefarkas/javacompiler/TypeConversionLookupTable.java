package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.irgeneration.SemanticAnalysisVisitor;
import com.moshefarkas.javacompiler.irgeneration.SemanticAnalysisVisitor.Type;

public class TypeConversionLookupTable {
    private static Type[][] table = {
        {Type.INT, Type.FLOAT, Type.INT},
        {Type.FLOAT, Type.FLOAT, Type.FLOAT},
        {Type.INT, Type.FLOAT, Type.INT},
    };

    public static Type ExpectedType(Type a, Type b) {
        int row = 0, col = 0;
        switch (a) {
            case INT:
                row = 0; 
                break;
            case FLOAT:
                row = 1;    
                break;
            case CHAR:
                row = 2;
                break;
            default:
                throw new UnsupportedOperationException("fix for: " + a);
        }
        switch (b) {
            case INT:
                col = 0; 
                break;
            case FLOAT:
                col = 1;    
                break;
            case CHAR:
                col = 2;
                break;
            default:
                throw new UnsupportedOperationException("fix for: " + a);
        }
        return table[row][col];
    }
}
