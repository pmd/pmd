package net.sourceforge.pmd;

import java.util.*;

public interface Rule {
    public String getName();
    public String getDescription();
    public void setName(String name);
    public void setDescription(String description);
    public void apply(List astCompilationUnits, RuleContext ctx);
}
