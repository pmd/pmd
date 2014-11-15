/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.regex.Pattern;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.StringUtil;

public class ParametricRuleViolation<T extends Node> implements RuleViolation {

    protected final Rule rule;
    protected final String description;
    protected boolean suppressed;
    protected String filename;

    protected int beginLine;
    protected int beginColumn;

    protected int endLine;
    protected int endColumn;

    protected String packageName = "";
    protected String className = "";
    protected String methodName = "";
    protected String variableName = "";

    // FUTURE Fix to understand when a violation _must_ have a Node, and when it must not (to prevent erroneous Rules silently logging w/o a Node).  Modify RuleViolationFactory to support identifying without a Node, and update Rule base classes too.
    public ParametricRuleViolation(Rule theRule, RuleContext ctx, T node, String message) {
	rule = theRule;
	description = message;
	filename = ctx.getSourceCodeFilename();
	if (filename == null) {
	    filename = "";
	}
	if (node != null) {
	    beginLine = node.getBeginLine();
	    beginColumn = node.getBeginColumn();
	    endLine = node.getEndLine();
	    endColumn = node.getEndColumn();
	}

	// Apply Rule specific suppressions
	if (node != null && rule != null) {
		setSuppression(rule, node);
	}
	
    }

    private void setSuppression(Rule rule, T node) {
    
    	String regex = rule.getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);	// Regex
    	if (regex != null && description != null) {
    		if (Pattern.matches(regex, description)) {
    			suppressed = true;
    		}
    	}
    
    	if (!suppressed) {	// XPath
    		String xpath = rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
    		if (xpath != null) {
    			suppressed = node.hasDescendantMatchingXPath(xpath);
    		}
    	}
    }

    protected String expandVariables(String message) {
    	
    	if (message.indexOf("${") < 0) {
    	    return message;
    	}
    	
	    StringBuilder buf = new StringBuilder(message);
	    int startIndex = -1;
	    while ((startIndex = buf.indexOf("${", startIndex + 1)) >= 0) {
			final int endIndex = buf.indexOf("}", startIndex);
			if (endIndex >= 0) {
			    final String name = buf.substring(startIndex + 2, endIndex);
			    if (isVariable(name)) {
			    	buf.replace(startIndex, endIndex + 1, getVariableValue(name));
			    	}
				}
		    }
	    return buf.toString();	 
    }

    protected boolean isVariable(String name) {
    	return 
    		StringUtil.isAnyOf(name, "variableName", "methodName", "className", "packageName") ||
    		rule.getPropertyDescriptor(name) != null;
    }

    protected String getVariableValue(String name) {
	if ("variableName".equals(name)) {
	    return variableName;
	} else if ("methodName".equals(name)) {
	    return methodName;
	} else if ("className".equals(name)) {
	    return className;
	} else if ("packageName".equals(name)) {
	    return packageName;
	} else {
	    final PropertyDescriptor<?> propertyDescriptor = rule.getPropertyDescriptor(name);
	    return String.valueOf(rule.getProperty(propertyDescriptor));
	}
    }

    public Rule getRule() {
	return rule;
    }

    public String getDescription() {
	return expandVariables(description);
    }

    public boolean isSuppressed() {
	return suppressed;
    }

    public String getFilename() {
	return filename;
    }

    public int getBeginLine() {
	return beginLine;
    }

    public int getBeginColumn() {
	return beginColumn;
    }

    public int getEndLine() {
	return endLine;
    }

    public int getEndColumn() {
	return endColumn;
    }

    public String getPackageName() {
	return packageName;
    }

    public String getClassName() {
	return className;
    }

    public String getMethodName() {
	return methodName;
    }

    public String getVariableName() {
	return variableName;
    }

    public void setLines(int theBeginLine, int theEndLine) {
    	beginLine = theBeginLine;
    	endLine = theEndLine;
    }
    
    @Override
    public String toString() {
	return getFilename() + ':' + getRule() + ':' + getDescription() + ':' + beginLine;
    }
}
