/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.test.PmdRuleTst;
import org.junit.After;
import org.junit.Before;

class UnitTestShouldUseAfterAnnotationTest extends PmdRuleTst {
    // no additional unit tests

    public static class BaseTest {
        @Before
        public void setUp() {
            // A setup code
        }

        @After
        public void tearDown() {
            // A tear down code
        }
    }
}
