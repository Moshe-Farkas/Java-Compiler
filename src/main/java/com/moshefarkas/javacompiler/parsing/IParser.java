package com.moshefarkas.javacompiler.parsing;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public interface IParser {
    // both string parser and file parser have to be able to parse to ast
    public ClassNode parse(String format) throws Exception;
} 