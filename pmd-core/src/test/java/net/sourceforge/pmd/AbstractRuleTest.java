/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.StringProperty;


public class AbstractRuleTest {

    public static class MyRule extends AbstractRule {
        private static final StringProperty FOO_PROPERTY = new StringProperty("foo", "foo property", "x", 1.0f);
        private static final PropertyDescriptor<String> FOO_DEFAULT_PROPERTY = PropertyFactory.stringProperty("fooDefault")
                .defaultValue("bar")
                .desc("Property without value uses default value")
                .build();

        private static final StringProperty XPATH_PROPERTY = new StringProperty("xpath", "xpath property", "", 2.0f);

        public MyRule() {
            definePropertyDescriptor(FOO_PROPERTY);
            definePropertyDescriptor(XPATH_PROPERTY);
            definePropertyDescriptor(FOO_DEFAULT_PROPERTY);
            setName("MyRule");
            setMessage("my rule msg");
            setPriority(RulePriority.MEDIUM);
            setProperty(FOO_PROPERTY, "value");
        }

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
        }
    }

    private static class MyOtherRule extends AbstractRule {
        private static final PropertyDescriptor FOO_PROPERTY = new StringProperty("foo", "foo property", "x", 1.0f);

        MyOtherRule() {
            definePropertyDescriptor(FOO_PROPERTY);
            setName("MyOtherRule");
            setMessage("my other rule");
            setPriority(RulePriority.MEDIUM);
            setProperty(FOO_PROPERTY, "value");
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
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginColumn(5);
        s.testingOnlySetBeginLine(5);
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
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginColumn(5);
        s.testingOnlySetBeginLine(5);
        RuleViolation rv = new ParametricRuleViolation<>(r, ctx, s, "specificdescription");
        assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "specificdescription", rv.getDescription());
    }

    @Test
    public void testRuleWithVariableInMessage() {
        MyRule r = new MyRule();
        r.definePropertyDescriptor(PropertyFactory.intProperty("testInt").desc("description").require(inRange(0, 100)).defaultValue(10).build());
        r.setMessage("Message ${packageName} ${className} ${methodName} ${variableName} ${testInt} ${noSuchProperty}");
        RuleContext ctx = new RuleContext();
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        ctx.setReport(new Report());
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginColumn(5);
        s.testingOnlySetBeginLine(5);
        s.setImage("TestImage");
        r.addViolation(ctx, s);
        RuleViolation rv = ctx.getReport().getViolationTree().iterator().next();
        assertEquals("Message foo    10 ${noSuchProperty}", rv.getDescription());
    }

    @Test
    public void testRuleSuppress() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        Map<Integer, String> m = new HashMap<>();
        m.put(Integer.valueOf(5), "");
        ctx.setReport(new Report());
        ctx.getReport().suppress(m);
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode n = new DummyNode(1);
        n.testingOnlySetBeginColumn(5);
        n.testingOnlySetBeginLine(5);
        RuleViolation rv = new ParametricRuleViolation<>(r, ctx, n, "specificdescription");
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
        r1.setProperty(MyRule.XPATH_PROPERTY, "something");
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.XPATH_PROPERTY, "something else");
        assertFalse("Rules with different properties values cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals9() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.XPATH_PROPERTY, "something else");
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

    @Test
    public void testDeepCopyRule() {
        MyRule r1 = new MyRule();
        MyRule r2 = (MyRule) r1.deepCopy();
        assertEquals(r1.getDescription(), r2.getDescription());
        assertEquals(r1.getExamples(), r2.getExamples());
        assertEquals(r1.getExternalInfoUrl(), r2.getExternalInfoUrl());
        assertEquals(r1.getLanguage(), r2.getLanguage());
        assertEquals(r1.getMaximumLanguageVersion(), r2.getMaximumLanguageVersion());
        assertEquals(r1.getMessage(), r2.getMessage());
        assertEquals(r1.getMinimumLanguageVersion(), r2.getMinimumLanguageVersion());
        assertEquals(r1.getName(), r2.getName());
        assertEquals(r1.getPriority(), r2.getPriority());
        assertEquals(r1.getPropertyDescriptors(), r2.getPropertyDescriptors());
        assertEquals(r1.getRuleChainVisits(), r2.getRuleChainVisits());
        assertEquals(r1.getRuleClass(), r2.getRuleClass());
        assertEquals(r1.getRuleSetName(), r2.getRuleSetName());
        assertEquals(r1.getSince(), r2.getSince());

        assertEquals(r1.isPropertyOverridden(MyRule.FOO_DEFAULT_PROPERTY),
                r2.isPropertyOverridden(MyRule.FOO_DEFAULT_PROPERTY));
    }
}
