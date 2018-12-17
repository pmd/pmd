/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetricsHook;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class CyclomaticComplexityTest extends PmdRuleTst {
    @Override
    protected Rule reinitializeRule(Rule rule) {
        ApexMetricsHook.reset();
        return super.reinitializeRule(rule);
    }
}
