/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

import org.junit.Test;

public class AbstractRuleTest {

    private static class MyRule extends AbstractRule {
        private static final StringProperty pd = new StringProperty("foo", "foo property", "x", 1.0f);

        private static final StringProperty xpath = new StringProperty("xpath", "xpath property", "", 2.0f);

        public MyRule() {
            definePropertyDescriptor(pd);
            definePropertyDescriptor(xpath);
            setName("MyRule");
            setMessage("my rule msg");
            setPriority(RulePriority.MEDIUM);
            setProperty(pd, "value");
        }

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
        }
    }

    private static class MyOtherRule extends AbstractRule {
        private static final PropertyDescriptor pd = new StringProperty("foo", "foo property", "x", 1.0f);

        public MyOtherRule() {
            definePropertyDescriptor(pd);
            setName("MyOtherRule");
            setMessage("my other rule");
            setPriority(RulePriority.MEDIUM);
            setProperty(pd, "value");
        }

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
        }
    }

    @Test
    public void testCreateRV() {
        MyRule r = new MyRule();
        r.setRuleSetName("foo");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        RuleViolation rv = new ParametricRuleViolation(r, ctx, s, r.getMessage());
        assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule msg mismatch!", "my rule msg", rv.getDescription());
        assertEquals("RuleSet name mismatch!", "foo", rv.getRule().getRuleSetName());
    }

    @Test
    public void testCreateRV2() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        RuleViolation rv = new ParametricRuleViolation(r, ctx, s, "specificdescription");
        assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "specificdescription", rv.getDescription());
    }

    @Test
    public void testRuleWithVariableInMessage() {
        MyRule r = new MyRule();
        r.definePropertyDescriptor(new IntegerProperty("testInt", "description", 0, 100, 10, 0));
        r.setMessage("Message ${packageName} ${className} ${methodName} ${variableName} ${testInt} ${noSuchProperty}");
        RuleContext ctx = new RuleContext();
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        ctx.setReport(new Report());
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        s.testingOnly__setBeginColumn(5);
        s.testingOnly__setBeginLine(5);
        s.setImage("TestImage");
        r.addViolation(ctx, s);
        RuleViolation rv = ctx.getReport().getViolationTree().iterator().next();
        assertEquals("Message foo    10 ${noSuchProperty}", rv.getDescription());
    }

    @Test
    public void testRuleSuppress() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(Integer.valueOf(5), "");
        ctx.setReport(new Report());
        ctx.getReport().suppress(m);
        ctx.setSourceCodeFilename("filename");
        DummyNode n = new DummyNode(1);
        n.testingOnly__setBeginColumn(5);
        n.testingOnly__setBeginLine(5);
        RuleViolation rv = new ParametricRuleViolation(r, ctx, n, "specificdescription");
        ctx.getReport().addRuleViolation(rv);
        assertTrue(ctx.getReport().isEmpty());
    }

    @Test
    public void testEquals1() {
        MyRule r = new MyRule();
        assertFalse("A rule is never equals to null!", r.equals(null));
    }

    @Test
    public void testEquals2() {
        MyRule r = new MyRule();
        assertEquals("A rule must be equals to itself", r, r);
    }

    @Test
    public void testEquals3() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        assertEquals("Two instances of the same rule are equal", r1, r2);
        assertEquals("Hashcode for two instances of the same rule must be equal", r1.hashCode(), r2.hashCode());
    }

    @Test
    public void testEquals4() {
        MyRule myRule = new MyRule();
        assertFalse("A rule cannot be equal to an object of another class", myRule.equals("MyRule"));
    }

    @Test
    public void testEquals5() {
        MyRule myRule = new MyRule();
        MyOtherRule myOtherRule = new MyOtherRule();
        assertFalse("Two rules from different classes cannot be equal", myRule.equals(myOtherRule));
    }

    @Test
    public void testEquals6() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setName("MyRule2");
        assertFalse("Rules with different names cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals7() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setPriority(RulePriority.HIGH);
        assertFalse("Rules with different priority levels cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals8() {
        MyRule r1 = new MyRule();
        r1.setProperty(MyRule.xpath, "something");
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.xpath, "something else");
        assertFalse("Rules with different properties values cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals9() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.xpath, "something else");
        assertFalse("Rules with different properties cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals10() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setMessage("another message");
        assertEquals("Rules with different messages are still equal", r1, r2);
        assertEquals("Rules that are equal must have the an equal hashcode", r1.hashCode(), r2.hashCode());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AbstractRuleTest.class);
    }
}
