package net.sourceforge.pmd;

import java.util.*;

public interface Rule {
    public String getName();
    public String getMessage();
    public String getDescription();
    public void setName(String name);
    public void setMessage(String message);
    public void setDescription(String description);
    public void apply(List astCompilationUnits, RuleContext ctx);
    public void addProperty(String name, String value);
    public int getIntProperty(String name);
    public boolean getBooleanProperty(String name);
    public String getStringProperty(String name);
}
