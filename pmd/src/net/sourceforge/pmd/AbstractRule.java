/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public abstract class AbstractRule extends JavaParserVisitorAdapter implements Rule {

    protected String name = getClass().getName();
    protected Properties properties = new Properties();
    protected String message;
    protected String description;
    protected String example;
    protected String ruleSetName;
    protected boolean m_include;
    protected int priority = LOWEST_PRIORITY;

    public String getRuleSetName() {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public void addProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    public double getDoubleProperty(String name) {
        return Double.parseDouble(properties.getProperty(name));
    }

    public int getIntProperty(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }

    public boolean getBooleanProperty(String name) {
        return Boolean.valueOf(properties.getProperty(name)).booleanValue();
    }

    public String getStringProperty(String name) {
        return properties.getProperty(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Rule)) {
            return false;
        }
        return ((Rule)o).getName().equals(getName());
    }

    public int hashCode() {
        return getName().hashCode();
    }

    protected void visitAll(List acus, RuleContext ctx) {
        for (Iterator i = acus.iterator(); i.hasNext();) {
            ASTCompilationUnit node = (ASTCompilationUnit) i.next();
            visit(node, ctx);
        }
    }

    public void apply(List acus, RuleContext ctx) {
        visitAll(acus, ctx);
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber) {
        return new RuleViolation(this, lineNumber, ctx);
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber, String specificDescription) {
        return new RuleViolation(this, lineNumber, specificDescription, ctx);
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean include() {
        return m_include;
    }

    public void setInclude(boolean include) {
        m_include = include;
    }

    public int getPriority() {
        return priority;
    }

    public String getPriorityName() {
        return PRIORITIES[getPriority() - 1];
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
