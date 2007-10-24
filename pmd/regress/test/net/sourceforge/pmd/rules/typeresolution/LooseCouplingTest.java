/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.typeresolution;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class LooseCouplingTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("typeresolution", "LooseCoupling");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LooseCouplingTest.class);
    }
}
