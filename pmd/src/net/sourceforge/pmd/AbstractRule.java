/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 5:44:22 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.List;
import java.util.Iterator;
import java.util.Properties;

public abstract class AbstractRule 
    extends JavaParserVisitorAdapter
    implements Rule
{

    private String name = getClass().getName();
    private Properties properties = new Properties();
    private String message;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public boolean hasProperty( String name ) {
		return properties.containsKey( name );	
	}

    public void addProperty(String name, String value) {
        properties.put(name, value);
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
        return new RuleViolation(this, lineNumber, ctx.getSourceCodeFilename());
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber, String specificDescription) {
        return new RuleViolation(this, lineNumber, specificDescription, ctx.getSourceCodeFilename());
    }

}
