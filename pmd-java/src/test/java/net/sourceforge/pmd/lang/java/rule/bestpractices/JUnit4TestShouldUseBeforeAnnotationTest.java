/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.junit.After;
import org.junit.Before;

import net.sourceforge.pmd.test.PmdRuleTst;

class JUnit4TestShouldUseBeforeAnnotationTest extends PmdRuleTst {
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
