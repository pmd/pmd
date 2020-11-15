/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.Reportable;

/**
 * @deprecated This is internal. Clients should exclusively use {@link RuleViolation}.
 */
@Deprecated
@InternalApi
public class ParametricRuleViolation implements RuleViolation {
    // todo move to package reporting

    protected final Rule rule;
    protected final String description;

    private final FileLocation location;

    protected String packageName = "";
    protected String className = "";
    protected String methodName = "";
    protected String variableName = "";


    public ParametricRuleViolation(Rule theRule, Reportable node, String message) {
        this(theRule, node.getReportLocation(), message);
    }

    public ParametricRuleViolation(Rule theRule, FileLocation location, String message) {
        this.rule = AssertionUtil.requireParamNotNull("rule", theRule);
        this.description = AssertionUtil.requireParamNotNull("message", message);

        this.location = location;
    }

    protected String expandVariables(String message) {
        // TODO move that to RuleContext with the rest of the formatting logic

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
    public FileLocation getLocation() {
        return location;
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

    @Override
    public String toString() {
        return getFilename() + ':' + getRule() + ':' + getDescription() + ':' + getLocation().startPosToString();
    }
}
