/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class ParametricRuleViolation<T extends Node> implements RuleViolation {

    protected final Rule rule;
    protected final String description;
    protected String filename;

    protected int beginLine;
    protected int beginColumn;

    protected int endLine;
    protected int endColumn;

    protected String packageName = "";
    protected String className = "";
    protected String methodName = "";
    protected String variableName = "";

    // FUTURE Fix to understand when a violation _must_ have a Node, and when it
    // must not (to prevent erroneous Rules silently logging w/o a Node). Modify
    // RuleViolationFactory to support identifying without a Node, and update
    // Rule base classes too.
    // TODO we never need a node. We just have to have a "position", ie line/column, or offset, + file, whatever
    public ParametricRuleViolation(Rule theRule, String filename, T node, String message) {
        rule = theRule;
        description = message;
        this.filename = filename == null ? "" : filename;

        if (node != null) {
            beginLine = node.getBeginLine();
            beginColumn = node.getBeginColumn();
            endLine = node.getEndLine();
            endColumn = node.getEndColumn();
        }

    }

    protected String expandVariables(String message) {

        if (!message.contains("${")) {
            return message;
        }

        StringBuilder buf = new StringBuilder(message);
        int startIndex = -1;
        while ((startIndex = buf.indexOf("${", startIndex + 1)) >= 0) {
            final int endIndex = buf.indexOf("}", startIndex);
            if (endIndex >= 0) {
                final String name = buf.substring(startIndex + 2, endIndex);
                String variableValue = getVariableValue(name);
                if (variableValue != null) {
                    buf.replace(startIndex, endIndex + 1, variableValue);
                }
            }
        }
        return buf.toString();
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
            return propertyDescriptor == null ? null : String.valueOf(rule.getProperty(propertyDescriptor));
        }
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public String getDescription() {
        return expandVariables(description);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
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
