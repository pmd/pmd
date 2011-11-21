package net.sourceforge.pmd.lang.java.ast;

public interface Dimensionable {
    boolean isArray();

    int getArrayDepth();
}
