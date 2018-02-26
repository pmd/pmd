/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;

public class RuleViolationFixContextTest {
    private static final int UNIMPORTANT_ID = 0;

    private Node dummyNode;

    @Before
    public void setUp() {
        dummyNode = new DummyNode(UNIMPORTANT_ID);
    }

    @Test
    public void instanceDummyRuleViolationWithParameterlessConstructorFixShouldSucceed()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final RuleViolationFixContext fixContext = new RuleViolationFixContext(DummyRuleViolationFix.class, dummyNode);
        final String privateMethodName = "instanceRuleViolationFixAndApply";
        final Method privateMethod = fixContext.getClass().getDeclaredMethod(privateMethodName);
        privateMethod.setAccessible(true);
        privateMethod.invoke(fixContext);
    }

    @Test(expected = NullPointerException.class)
    public void nullClassShouldThrowNullPointerExceptionUponInstantiationOfContext() {
        new RuleViolationFixContext(null, dummyNode);
    }

    @Test(expected = NullPointerException.class)
    public void nullNodeShouldThrowNullPointerExceptionUponInstantiationOfContext() {
        new RuleViolationFixContext(DummyRuleViolationFix.class, null);
    }
}
