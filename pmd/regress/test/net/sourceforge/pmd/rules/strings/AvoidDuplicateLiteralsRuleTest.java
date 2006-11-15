
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.strings;
 
 import java.util.Set;
 
 import net.sourceforge.pmd.Rule;
 import net.sourceforge.pmd.rules.strings.AvoidDuplicateLiteralsRule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidDuplicateLiteralsRuleTest extends SimpleAggregatorTst {
     public void testAll() {
         Rule rule = findRule("strings", "AvoidDuplicateLiterals");
         rule.addProperty("threshold", "2");
         runTests(rule);
     }
 
     public void testStringParserEmptyString() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("");
         assertTrue(res.isEmpty());
     }
 
     public void testStringParserSimple() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("a,b,c");
         assertEquals(3, res.size());
         assertTrue(res.contains("a"));
         assertTrue(res.contains("b"));
         assertTrue(res.contains("c"));
     }
 
     public void testStringParserEscapedChar() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("a,b,\\,");
         assertEquals(3, res.size());
         assertTrue(res.contains("a"));
         assertTrue(res.contains("b"));
         assertTrue(res.contains(","));
     }
 
     public void testStringParserEscapedEscapedChar() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("a,b,\\\\");
         assertEquals(3, res.size());
         assertTrue(res.contains("a"));
         assertTrue(res.contains("b"));
         assertTrue(res.contains("\\"));
     }
 }
