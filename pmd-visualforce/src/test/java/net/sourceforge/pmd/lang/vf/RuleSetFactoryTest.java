/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.AbstractRuleSetFactoryTest;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;

public class RuleSetFactoryTest extends AbstractRuleSetFactoryTest {
    public RuleSetFactoryTest() {
        super(new ApexLanguageModule());
    }
}
