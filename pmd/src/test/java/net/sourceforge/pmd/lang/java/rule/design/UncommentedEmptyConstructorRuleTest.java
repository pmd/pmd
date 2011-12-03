
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package net.sourceforge.pmd.lang.java.rule.design;
 
 import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import net.sourceforge.pmd.testframework.TestDescriptor;

import org.junit.Before;
import org.junit.Test;

 
 public class UncommentedEmptyConstructorRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
     private TestDescriptor[] tests;
 
     @Before
     public void setUp() {
         rule = findRule("java-design", "UncommentedEmptyConstructor");
         tests = extractTestsFromXml(rule);
     }
 
     @Test
     public void testDefault() {
         runTests(tests);
     }
 
     @Test
     public void testIgnoredConstructorInvocation() {
	 PropertyDescriptor<Boolean> descriptor = (PropertyDescriptor<Boolean>)rule.getPropertyDescriptor("ignoreExplicitConstructorInvocation");
         rule.setProperty(descriptor, true);
         TestDescriptor[] testDescriptors = new TestDescriptor[] {
		new TestDescriptor(tests[0].getCode(), "simple failure", 1, rule),
		new TestDescriptor(tests[1].getCode(), "only 'this(...)' failure", 1, rule),
		new TestDescriptor(tests[2].getCode(), "only 'super(...)' failure", 1, rule),
		new TestDescriptor(tests[3].getCode(), "single-line comment is OK", 0, rule),
		new TestDescriptor(tests[4].getCode(), "multiple-line comment is OK", 0, rule),
		new TestDescriptor(tests[5].getCode(), "Javadoc comment is OK", 0, rule),
		new TestDescriptor(tests[6].getCode(), "ok", 0, rule),
		new TestDescriptor(tests[7].getCode(), "with 'this(...)' ok", 0, rule),
		new TestDescriptor(tests[8].getCode(), "with 'super(...)' ok", 0, rule),
		new TestDescriptor(tests[9].getCode(), "private is ok", 0, rule), };
         for (TestDescriptor testDescriptor : testDescriptors) {
             testDescriptor.setReinitializeRule(false);
         }
         runTests(testDescriptors);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(UncommentedEmptyConstructorRuleTest.class);
     }
 }
