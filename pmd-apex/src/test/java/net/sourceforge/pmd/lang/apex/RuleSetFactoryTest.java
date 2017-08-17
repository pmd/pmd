/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.AbstractRuleSetFactoryTest;
import net.sourceforge.pmd.lang.apex.rule.ApexXPathRule;

public class RuleSetFactoryTest extends AbstractRuleSetFactoryTest {
    public RuleSetFactoryTest() {
        super();
        validXPathClassNames.add(ApexXPathRule.class.getName());
    }
}
