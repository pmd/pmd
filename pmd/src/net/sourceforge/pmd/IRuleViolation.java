package net.sourceforge.pmd;

public interface IRuleViolation {
    String getFilename();
    int getBeginLine();
    int getBeginColumn();
    int getEndLine();
    int getEndColumn();
    Rule getRule();
    String getDescription();
    String getPackageName();
    String getMethodName();
    String getClassName();
    boolean isSuppressed();
    String getVariableName();
}
