/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleNode;

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
    protected boolean include;
    protected boolean usesDFA;
    protected boolean usesSymbolTable;
    protected int priority = LOWEST_PRIORITY;
    private String packageName;
    private String className;
    private String methodName;

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

    public boolean equals(Object o) {
        if (!(o instanceof Rule)) {
            return false;
        }
        return ((Rule) o).getName().equals(getName());
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

    /**
     * @deprecated use @link #createRuleViolation(RuleContext, IPositionProvider) instead 
     */
    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber) {
        return new RuleViolation(this, lineNumber, ctx, packageName, className, methodName);
    }

    public RuleViolation createRuleViolation(RuleContext ctx, IPositionProvider pp) {
        RuleViolation v = new RuleViolation(this, ctx, packageName, className, methodName);
        extractNodeInfo(v, pp);
        return v;
    }

    /**
     * @deprecated use @link #createRuleViolation(RuleContext, IPositionProvider, String) instead
     */
    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber, String specificDescription) {
        return new RuleViolation(this, lineNumber, specificDescription, ctx, packageName, className, methodName);
    }

    public RuleViolation createRuleViolation(RuleContext ctx, IPositionProvider pp, String specificDescription) {
        RuleViolation rv = new RuleViolation(this, 0, specificDescription, ctx, packageName, className, methodName);
        extractNodeInfo(rv, pp);
        return rv;
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber, int lineNumber2, String variableName, String specificDescription) {
        return new RuleViolation(this, lineNumber, lineNumber2, variableName, specificDescription, ctx, packageName, className, methodName);
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

    public Object visit(ASTPackageDeclaration node, Object data) {
        packageName = ((ASTName) node.jjtGetChild(0)).getImage();
        return super.visit(node, data);
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        className = node.getImage();
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        methodName = node.getImage();
        return super.visit(node, data);
    }

    public void setUsesSymbolTable() {
        this.usesSymbolTable = true;
    }

    public boolean usesSymbolTable() {
        return this.usesSymbolTable;
    }

    public void setUsesDFA() {
        this.usesDFA = true;
    }

    public boolean usesDFA() {
        return this.usesDFA;
    }

    /**
     * Adds a violation to the report.
     * It is an utility method that simply does:
     * <code>context.getReport().addRuleViolation(createRuleViolation(context, beginLine));</code>
     * 
     * @param context the RuleContext
     * @param beginLine begin line of the violation
     * @deprecated use @link #addViolation(RuleContext, IPositionProvider)
     */
    protected final void addViolation(RuleContext context, int beginLine) {
        context.getReport().addRuleViolation(createRuleViolation(context, beginLine));
    }

    /**
     * Adds a violation to the report.
     * 
     * @param context the RuleContext
     * @param pp the node that produces the violation, may be null, in which case all line and column info will be set to zero
     */
    protected final void addViolation(RuleContext context, IPositionProvider pp) {
        context.getReport().addRuleViolation(createRuleViolation(context, pp));
    }

    /** 
     * Gets the Image of the first parent node of type ASTClassOrInterfaceDeclaration or <code>null</code>
     *  
     * @param node the node which will be searched
     * @return
     */
    protected final String getDeclaringType(SimpleNode  node) {
		ASTClassOrInterfaceDeclaration c = (ASTClassOrInterfaceDeclaration) node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
		if (c!=null)
			return c.getImage();
		return null;
	}

    private final void extractNodeInfo(RuleViolation v, IPositionProvider pp) {
        if (pp==null) {
            v.setLine(0);
            v.setColumnInfo(0, 0);
        } else {
            v.setLine(pp.getBeginLine());
            v.setColumnInfo(pp.getBeginColumn(), pp.getEndColumn());
        }
    }

}
