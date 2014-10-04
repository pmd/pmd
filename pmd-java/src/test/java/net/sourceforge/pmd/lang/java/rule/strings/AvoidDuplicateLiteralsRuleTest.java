/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Test;

public class AvoidDuplicateLiteralsRuleTest extends SimpleAggregatorTst {
    @Test
    public void testAll() {
        Rule rule = findRule("java-strings", "AvoidDuplicateLiterals");
        rule.setProperty(AvoidDuplicateLiteralsRule.THRESHOLD_DESCRIPTOR, 2);
        runTests(rule);
    }

    @Test
    public void testStringParserEmptyString() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("");
        assertTrue(res.isEmpty());
    }

    @Test
    public void testStringParserSimple() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("a,b,c");
        assertEquals(3, res.size());
        assertTrue(res.contains("a"));
        assertTrue(res.contains("b"));
        assertTrue(res.contains("c"));
    }

    @Test
    public void testStringParserEscapedChar() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("a,b,\\,");
        assertEquals(3, res.size());
        assertTrue(res.contains("a"));
        assertTrue(res.contains("b"));
        assertTrue(res.contains(","));
    }

    @Test
    public void testStringParserEscapedEscapedChar() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set<String> res = p.parse("a,b,\\\\");
        assertEquals(3, res.size());
        assertTrue(res.contains("a"));
        assertTrue(res.contains("b"));
        assertTrue(res.contains("\\"));
    }
}
