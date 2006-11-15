/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UnsynchronizedStaticDateFormatterTest extends SimpleAggregatorTst  {

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "UnsynchronizedStaticDateFormatter");
    }

    public void testAll() throws Exception{
        runTests(rule);
    }
}