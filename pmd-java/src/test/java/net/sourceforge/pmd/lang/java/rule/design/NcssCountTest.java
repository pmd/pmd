/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.java.metrics.MetricsHook;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class NcssCountTest extends PmdRuleTst {
    @Override
    protected Rule reinitializeRule(Rule rule) {
        MetricsHook.reset();
        return rule;
    }
}
