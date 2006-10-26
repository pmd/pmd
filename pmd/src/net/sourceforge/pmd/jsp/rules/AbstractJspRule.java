/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.jsp.ast.JspParserVisitorAdapter;
import net.sourceforge.pmd.jsp.ast.SimpleNode;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public abstract class AbstractJspRule extends JspParserVisitorAdapter implements Rule {

    protected String name = getClass().getName();
    protected Properties properties = new Properties();
    protected String message;
    protected String description;
    protected String example;
    protected String ruleSetName;
    protected boolean include;
    protected boolean usesDFA;
    protected boolean usesTypeResolution;
    protected int priority = LOWEST_PRIORITY;
    protected String externalInfoUrl;

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

    public void addProperties(Properties properties) {
        this.properties.putAll(properties);
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

    public String getExternalInfoUrl() {
        return externalInfoUrl;
    }

    public void setExternalInfoUrl(String url) {
        this.externalInfoUrl = url;
    }

    /**
     * Test if rules are equals. Rules are equals if
     * 1. they have the same implementation class
     * 2. they have the same name
     * 3. they have the same priority
     * 4. they share the same properties/values
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false; // trivial
        }

        if (this == o) {
            return true;  // trivial
        }

        Rule rule = null;
        boolean equality = this.getClass().getName().equals(o.getClass().getName());

        if (equality) {
            rule = (Rule) o;
            equality = this.getName().equals(rule.getName())
                    && this.getPriority() == rule.getPriority()
                    && this.getProperties().equals(rule.getProperties());
        }

        return equality;
    }

    /**
     * Return a hash code to conform to equality. Try with a string.
     */
    public int hashCode() {
        String s = this.getClass().getName() + this.getName() + this.getPriority() + this.getProperties().toString();
        return s.hashCode();
    }

    public void apply(List acus, RuleContext ctx) {
        visitAll(acus, ctx);
    }


    public Properties getProperties() {
        return properties;
    }

    public boolean include() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
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

    public void setUsesDFA() {
        this.usesDFA = true;
    }

    public boolean usesDFA() {
        return this.usesDFA;
    }

    public void setUsesTypeResolution() {
    }

    public boolean usesTypeResolution() {
        return false;
    }

    protected void visitAll(List acus, RuleContext ctx) {
        for (Iterator i = acus.iterator(); i.hasNext();) {
            SimpleNode node = (SimpleNode) i.next();
            visit(node, ctx);
        }
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation
     */
    protected final void addViolation(Object data, SimpleNode node) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node));
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation
     * @param msg  specific message to put in the report
     */
    protected final void addViolationWithMessage(Object data, SimpleNode node, String msg) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node, msg));
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx   the RuleContext
     * @param node  the node that produces the violation
     * @param embed a variable to embed in the rule violation message
     */
    protected final void addViolation(Object data, SimpleNode node, String embed) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node, MessageFormat.format(getMessage(), new Object[]{embed})));
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation, may be null, in which case all line and column info will be set to zero
     * @param args objects to embed in the rule violation message
     */
    protected final void addViolation(Object data, Node node, Object[] args) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, (SimpleNode) node, MessageFormat.format(getMessage(), args)));
    }
    
    public PropertyDescriptor propertyDescriptorFor(String name) {
    	return null;	// TODO not implemented yet
    }
}
