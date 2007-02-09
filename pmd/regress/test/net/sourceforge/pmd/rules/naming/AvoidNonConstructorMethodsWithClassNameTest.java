/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.naming;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class AvoidNonConstructorMethodsWithClassNameTest extends SimpleAggregatorTst {

    private Rule rule;

    @Before
    public void setUp() {
        rule = findRule("naming", "AvoidNonConstructorMethodsWithClassName");
    }

    @Test
    public void testAll() {
        runTests(rule);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AvoidNonConstructorMethodsWithClassNameTest.class);
    }
}
