package net.sourceforge.pmd;

import java.util.*;

public interface Rule {
    public String getName();
    public String getMessage();
    public void setName(String name);
    public void setMessage(String message);
    public void apply(List astCompilationUnits, RuleContext ctx);
}
