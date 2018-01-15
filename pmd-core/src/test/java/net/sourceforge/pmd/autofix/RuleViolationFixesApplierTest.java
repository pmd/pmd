/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;

public class RuleViolationFixesApplierTest {
    private static final int UNIMPORTANT_ID = 0;

    private RuleViolationFixesApplier applier;
    private Node dummyNode;

    @Before
    public void setUp() throws Exception {
        applier = new RuleViolationFixesApplier();
        dummyNode = new DummyNode(UNIMPORTANT_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void applyASecondTimeBeforeClearingShouldThrowException() {
        final Node dummyNode = new DummyNode(UNIMPORTANT_ID);

        applier.addRuleViolationFix(DummyRuleViolationFix.class, dummyNode);
        applier.applyAutoFixes();
        applier.applyAutoFixes();
    }

    @Test
    public void applyASecondTimeAfterClearingShouldSucceed() {
        applier.addRuleViolationFix(DummyRuleViolationFix.class, dummyNode);
        applier.applyAutoFixesAndClear();
        applier.applyAutoFixes();
    }

    @Test
    public void applyWronglyImplementedRuleViolationFixesShouldNotThrowException() {
        applier.addRuleViolationFix(DummyRuleViolationFix.class, dummyNode);
        applier.addRuleViolationFix(DummyWrongRuleViolationFix.class, dummyNode);
        applier.applyAutoFixes();
    }
}
