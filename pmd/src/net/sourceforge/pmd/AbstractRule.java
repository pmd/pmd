/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 5:44:22 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractRule
    extends JavaParserVisitorAdapter
    implements Rule
{

    private String name = getClass().getName();
    private RuleProperties properties = new RuleProperties();
    private String message;
    private String description;
    private String example;
    private boolean m_include;

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

    public boolean hasProperty( String name ) {
        return properties.containsKey( name );
    }

    public void addProperty(String name, String property) {
        properties.setProperty(name, property);
    }

    public double getDoubleProperty(String name) {
        return properties.getDoubleValue(name);
    }

    public int getIntProperty(String name) {
        return properties.getIntegerValue(name);
    }

    public boolean getBooleanProperty(String name) {
        return properties.getBooleanValue(name);
    }

    public String getStringProperty(String name) {
        return properties.getValue(name);
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
        Rule r = (Rule)o;
        return r.getName().equals(getName());
    }

    public int hashCode() {
        return getName().hashCode();
    }

    protected void visitAll( List acus, RuleContext ctx ) {
        for (Iterator i = acus.iterator(); i.hasNext();) {
           ASTCompilationUnit node = (ASTCompilationUnit)i.next();
           visit( node, ctx );
        }
    }

    public void apply( List acus, RuleContext ctx ) {
        visitAll( acus, ctx );
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber) {
        return new RuleViolation(this, lineNumber, ctx);
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber, String specificDescription) {
        return new RuleViolation(this, lineNumber, specificDescription, ctx);
    }

    /**
     ********************************************************************************
     *
     * Gets an enumeration to enumerate through this rule's property names.
     *
     * @return An enumeration of property names
     */
    public RuleProperties getProperties()
    {
        return properties;
    }

    /**
     *********************************************************************************
     *
     * When the rule is to be included in the analysis, returns true; otherwise, returns false.
     *
     * @return True when the rule is included in analysis.
     */
    public boolean include()
    {
        return m_include;
    }

    /**
     *********************************************************************************
     *
     * When the rule is to be included in the analysis, set to true; otherwise, set to false.
     *
     * @param include True when the rule is included in analysis.
     */
    public void setInclude(boolean include)
    {
        m_include = include;
    }
}
