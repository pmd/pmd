/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.AbstractRuleSetFactoryTest;
import net.sourceforge.pmd.lang.apex.rule.ApexXPathRule;

public class RuleSetFactoryTest extends AbstractRuleSetFactoryTest {
    public RuleSetFactoryTest() {
        super();
        // Copied from net.sourceforge.pmd.lang.apex.RuleSetFactoryTest
        // Apex rules are found in the classpath because this module has a dependency on pmd-apex
        validXPathClassNames.add(ApexXPathRule.class.getName());
    }
}
