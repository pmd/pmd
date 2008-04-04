package net.sourceforge.pmd.lang.java.ast;

public interface Dimensionable {
    public boolean isArray();

    public int getArrayDepth();
}
