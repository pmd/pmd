/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class AvoidDuplicateLiteralsTest extends PmdRuleTst {
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

    @Test
    public void testSeparatorPropertyWarning() throws Exception {
        AvoidDuplicateLiteralsRule rule = new AvoidDuplicateLiteralsRule();
        Assert.assertFalse(rule.isPropertyOverridden(AvoidDuplicateLiteralsRule.SEPARATOR_DESCRIPTOR));

        Rule copy = rule.deepCopy();
        Assert.assertFalse(copy.isPropertyOverridden(AvoidDuplicateLiteralsRule.SEPARATOR_DESCRIPTOR));
    }
}
