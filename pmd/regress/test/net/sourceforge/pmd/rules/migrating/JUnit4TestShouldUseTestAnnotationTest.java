/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.migrating;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class JUnit4TestShouldUseTestAnnotationTest extends SimpleAggregatorTst {
    private Rule rule;

    @Before
    public void setUp() {
        rule = findRule("migrating", "JUnit4TestShouldUseTestAnnotation");
    }

    @Test
    public void testAll() throws Throwable {
        runTests(rule);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JUnit4TestShouldUseTestAnnotationTest.class);
    }
}
