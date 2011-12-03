/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.typeresolution;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class CloneMethodMustImplementCloneableTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-typeresolution";

    @Before
	public void setUp() {
	//	addRule(RULESET, "CloneMethodMustImplementCloneable");
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CloneMethodMustImplementCloneableTest.class);
    }
}
