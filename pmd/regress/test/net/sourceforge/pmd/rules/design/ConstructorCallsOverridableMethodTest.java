/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ConstructorCallsOverridableMethodTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("design", "ConstructorCallsOverridableMethod");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ConstructorCallsOverridableMethodTest.class);
    }
}
