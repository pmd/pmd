/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.rule.codestyle.DuplicateImportsRule;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * @author guofei
 *
 */
public class JavaRuleViolationFactoryTest {

    @Test
    public void messageWithSingleBrace() {
        RuleViolationFactory factory = JavaRuleViolationFactory.INSTANCE;
        factory.addViolation(new RuleContext(), new DuplicateImportsRule(), null, "message with \"'{'\"", null);
    }

}
