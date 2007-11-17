package net.sourceforge.pmd;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

/**
 * Basic abstract implementation of all parser-independent methods of the
 * Rule interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public abstract class CommonAbstractRule implements Rule {
    private String name = getClass().getName();
    private Properties properties = new Properties();
    private String message;
    private String description;
    private List<String> examples = new ArrayList<String>();
    private String ruleSetName;
    private boolean include;
    private boolean usesDFA;
    private boolean usesTypeResolution;
    private int priority = LOWEST_PRIORITY;
    private String externalInfoUrl;
    private List<String> ruleChainVisits = new ArrayList<String>();

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

    public List<String> getExamples() {
        return examples;
    }
    
    /**
     * Still used by the JDeveloper plugin
     * 
     * @deprecated use getExamples(), since we now support multiple examples
     */
    public String getExample() {
        if (examples.isEmpty()) {
            return null;
        } else {
            //We return the last example, so the override still works
            return examples.get(examples.size()-1);
        }
    }

    public void addExample(String example) {
        examples.add(example);
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
        return Boolean.parseBoolean(properties.getProperty(name));
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
        this.usesTypeResolution= true;
    }

    public boolean usesTypeResolution() {
        return this.usesTypeResolution;
    }


    /**
     * Adds a violation to the report.
     *
     * @param data the RuleContext
     * @param node the node that produces the violation
     */
    protected final void addViolation(Object data, SimpleNode node) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node));
    }

    /**
     * Adds a violation to the report.
     *
     * @param data the RuleContext
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
     * @param data  the RuleContext
     * @param node  the node that produces the violation
     * @param embed a variable to embed in the rule violation message
     */
    protected final void addViolation(Object data, SimpleNode node, String embed) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node, MessageFormat.format(getMessage(), embed)));
    }

    /**
     * Adds a violation to the report.
     *
     * @param data the RuleContext
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

    public boolean usesRuleChain() {
        return !getRuleChainVisits().isEmpty();
    }

    public List<String> getRuleChainVisits() {
        return ruleChainVisits;
    }

    public void addRuleChainVisit(String astNodeName) {
        if (!ruleChainVisits.contains(astNodeName)) {
            ruleChainVisits.add(astNodeName);
        }
    }
}
