/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Adding this test to validate current working code doesn't break I've been
 * trying to locate the article referenced. The below code stresses the NPath
 * rule, and according to its current style, runs 2 tests, one pass and one
 * fail.
 * 
 * @author Allan Caplan
 */
public class UseCollectionIsEmptyTest extends SimpleAggregatorTst{

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "UseCollectionIsEmpty");
    }

    public void testAll() {
        runTests(rule);
    }

}

