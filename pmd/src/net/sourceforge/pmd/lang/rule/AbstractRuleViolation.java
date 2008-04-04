/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractRuleViolation implements RuleViolation {

    protected Rule rule;
    protected String description;
    protected boolean isSuppressed;
    protected String filename;

    protected int beginLine;
    protected int beginColumn;

    protected int endLine;
    protected int endColumn;

    protected String packageName;
    protected String className;
    protected String methodName;
    protected String variableName;

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
    }

    public Rule getRule() {
	return rule;
    }

    public String getDescription() {
	return description;
    }

    public boolean isSuppressed() {
	return this.isSuppressed;
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

    public String toString() {
	return getFilename() + ":" + getRule() + ":" + getDescription() + ":" + beginLine;
    }
}
