/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

/**
 *
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class TooManyHttpFilterTest extends SimpleAggregatorTst {

     private Rule rule;
     private TestDescriptor[] tests;

     @Before
     public void setUp() {
//         rule = findRule("design", "TooManyHttpFilter");
//         tests = extractTestsFromXml(rule);
     }

     @Test
     public void testDefault() {
 //        runTests(tests);
     }
     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(TooManyHttpFilterTest.class);
     }
 }
