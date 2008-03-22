/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnusedNullCheckInEqualsTest extends SimpleAggregatorTst {
     private Rule rule;

     @Before
     public void setUp() {
         rule = findRule("basic", "UnusedNullCheckInEquals");
     }

     @Test
     public void testAll() {
         runTests(rule);
     }

     @Ignore
     @Test
     public void testN(){
         runTest(new TestDescriptor(TESTN, "shouldn't this fail?", 1, rule));
     }

     private static final String TESTN =
             "public class Foo {" + PMD.EOL +
             " public void bar() {" + PMD.EOL +
             "  if (x != null && y.equals(x)) {} " + PMD.EOL +
             " }" + PMD.EOL +
             "}";


     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(UnusedNullCheckInEqualsTest.class);
     }
 }
