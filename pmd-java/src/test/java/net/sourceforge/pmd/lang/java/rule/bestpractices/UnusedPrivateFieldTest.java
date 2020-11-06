/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.testframework.PmdRuleTst;

public class UnusedPrivateFieldTest extends PmdRuleTst {

    /**
     * This test will fail, as soon Lombok classes are on the test classpath.
     * The test classpath is used as auxclasspath during unit tests.
     * If lombok is present, then the test case for #1952 will never fail
     * and won't reproduce the false-negative case anymore.
     */
    @Test
    public void makeSureLombokIsNotOnClasspath() {
        try {
            Class.forName("lombok.Value");
            Assert.fail();
        } catch (ClassNotFoundException e) {
            // this is ok
        }
    }
}
