/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.regex.Pattern;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractRuleViolation implements RuleViolation {

    protected Rule rule;
    protected String description;
    protected boolean suppressed;
    protected String filename;

    protected int beginLine;
    protected int beginColumn;

    protected int endLine;
    protected int endColumn;

    protected String packageName;
    protected String className;
    protected String methodName;
    protected String variableName;

    // FUTURE Fix to understand when a violation _must_ have a Node, and when it must not (to prevent erroneous Rules silently logging w/o a Node).  Modify RuleViolationFactory to support identifying without a Node, and update Rule base classes too.
    public AbstractRuleViolation(Rule rule, RuleContext ctx, Node node) {
	this(rule, ctx, node, rule.getMessage());
    }

    public AbstractRuleViolation(Rule rule, RuleContext ctx, Node node, String specificMsg) {
	this.rule = rule;
	this.description = specificMsg;
	this.filename = ctx.getSourceCodeFilename();
	if (this.filename == null) {
	    this.filename = "";
	}
	if (node != null) {
	    this.beginLine = node.getBeginLine();
	    this.beginColumn = node.getBeginColumn();
	    this.endLine = node.getEndLine();
	    this.endColumn = node.getEndColumn();
	}
	this.packageName = "";
	this.className = "";
	this.methodName = "";
	this.variableName = "";

	// Apply Rule specific suppressions
	if (node != null && rule != null) {
	    // Regex
	    String regex = rule.getStringProperty(Rule.VIOLATION_SUPPRESS_REGEX_PROPERTY);
	    if (regex != null && description != null) {
		if (Pattern.matches(regex, description)) {
		    suppressed = true;
		}
	    }

	    // XPath
	    if (!suppressed) {
		String xpath = rule.getStringProperty(Rule.VIOLATION_SUPPRESS_XPATH_PROPERTY);
		if (xpath != null) {
		    suppressed = node.hasDescendantMatchingXPath(xpath);
		}
	    }
	}
    }

    public Rule getRule() {
	return rule;
    }

    public String getDescription() {
	return description;
    }

    public boolean isSuppressed() {
	return this.suppressed;
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

    @Override
    public String toString() {
	return getFilename() + ":" + getRule() + ":" + getDescription() + ":" + beginLine;
    }
}
