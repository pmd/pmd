/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.reporting.Reportable;
import net.sourceforge.pmd.util.AssertionUtil;

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

    private final Map<String, String> additionalInfo;

    // todo add factory methods on the interface and hide the class.

    /**
     * @deprecated Update tests that use this not to call the ctor directly.
     */
    @Deprecated
    public ParametricRuleViolation(Rule theRule, Reportable node, String message) {
        this(theRule, node.getReportLocation(), message, Collections.emptyMap());
    }

    public ParametricRuleViolation(Rule theRule, FileLocation location, String message) {
        this(theRule, location, message, Collections.emptyMap());
    }

    public ParametricRuleViolation(Rule theRule, Reportable node, String message, Map<String, String> additionalInfo) {
        this(theRule, node.getReportLocation(), message, additionalInfo);
    }

    public ParametricRuleViolation(Rule theRule, FileLocation location, String message, Map<String, String> additionalInfo) {
        this.rule = AssertionUtil.requireParamNotNull("rule", theRule);
        this.description = AssertionUtil.requireParamNotNull("message", message);
        this.location = location;

        if (!additionalInfo.isEmpty()) {
            this.additionalInfo = Collections.unmodifiableMap(additionalInfo);
        } else {
            this.additionalInfo = Collections.emptyMap();
        }
    }

    @Override
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return getFilename() + ':' + getRule() + ':' + getDescription() + ':' + getLocation().startPosToString();
    }
}
