/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;

public class DummyRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final DummyRuleViolationFactory INSTANCE = new DummyRuleViolationFactory();
}
