package net.sourceforge.pmd.quickfix;

public interface Fix {
    String fix(String code, int lineNumber);
}
