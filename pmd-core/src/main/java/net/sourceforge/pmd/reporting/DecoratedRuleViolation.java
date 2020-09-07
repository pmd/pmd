/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.internal.util.AssertionUtil;

public final class DecoratedRuleViolation implements RuleViolation {
    // todo move to package reporting

    private final RuleViolation base;
    private final Map<String, String> info;

    public DecoratedRuleViolation(RuleViolation base, Map<String, String> additionalInfo) {
        this.base = AssertionUtil.requireParamNotNull("violation", base);
        this.info = Collections.unmodifiableMap(additionalInfo);
    }

    @Override
    public Map<String, String> getAdditionalInfo() {
        return info;
    }

    @Override
    public Rule getRule() {
        return base.getRule();
    }

    @Override
    public String getDescription() {
        return base.getDescription();
    }

    @Override
    public String getFilename() {
        return base.getFilename();
    }

    @Override
    public int getBeginLine() {
        return base.getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        return base.getBeginColumn();
    }

    @Override
    public int getEndLine() {
        return base.getEndLine();
    }

    @Override
    public int getEndColumn() {
        return base.getEndColumn();
    }

    @Override
    public String toString() {
        return "Decorated(" + base + ") with " + info;
    }
}
