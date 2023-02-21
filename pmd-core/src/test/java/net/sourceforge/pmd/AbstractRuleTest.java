/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.ReportTestUtil.getReportForRuleApply;
import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.FileAnalysisListener;


class AbstractRuleTest {

    private static class MyRule extends AbstractRule {
        private static final PropertyDescriptor<String> FOO_PROPERTY = PropertyFactory.stringProperty("foo").desc("foo property").defaultValue("x").build();
        private static final PropertyDescriptor<String> FOO_DEFAULT_PROPERTY = PropertyFactory.stringProperty("fooDefault")
                .defaultValue("bar")
                .desc("Property without value uses default value")
                .build();

        private static final PropertyDescriptor<String> XPATH_PROPERTY = PropertyFactory.stringProperty("xpath").desc("xpath property").defaultValue("").build();

        MyRule() {
            definePropertyDescriptor(FOO_PROPERTY);
            definePropertyDescriptor(XPATH_PROPERTY);
            definePropertyDescriptor(FOO_DEFAULT_PROPERTY);
            setName("MyRule");
            setMessage("my rule msg");
            setPriority(RulePriority.MEDIUM);
            setProperty(FOO_PROPERTY, "value");
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
        }
    }

    private static class MyOtherRule extends AbstractRule {
        private static final PropertyDescriptor<String> FOO_PROPERTY = PropertyFactory.stringProperty("foo").desc("foo property").defaultValue("x").build();

        MyOtherRule() {
            definePropertyDescriptor(FOO_PROPERTY);
            setName("MyOtherRule");
            setMessage("my other rule");
            setPriority(RulePriority.MEDIUM);
            setProperty(FOO_PROPERTY, "value");
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
        }
    }

    @RegisterExtension
    private final DummyParsingHelper helper = new DummyParsingHelper();

    @Test
    void testCreateRV() {
        MyRule r = new MyRule();
        r.setRuleSetName("foo");
        DummyRootNode s = helper.parse("abc()", "filename");

        RuleViolation rv = new ParametricRuleViolation(r, s, r.getMessage());
        assertEquals(1, rv.getBeginLine(), "Line number mismatch!");
        assertEquals("filename", rv.getFilename(), "Filename mismatch!");
        assertEquals(r, rv.getRule(), "Rule object mismatch!");
        assertEquals("my rule msg", rv.getDescription(), "Rule msg mismatch!");
        assertEquals("foo", rv.getRule().getRuleSetName(), "RuleSet name mismatch!");
    }

    @Test
    void testCreateRV2() {
        MyRule r = new MyRule();
        DummyRootNode s = helper.parse("abc()", "filename");
        RuleViolation rv = new ParametricRuleViolation(r, s, "specificdescription");
        assertEquals(1, rv.getBeginLine(), "Line number mismatch!");
        assertEquals("filename", rv.getFilename(), "Filename mismatch!");
        assertEquals(r, rv.getRule(), "Rule object mismatch!");
        assertEquals("specificdescription", rv.getDescription(), "Rule description mismatch!");
    }

    @Test
    void testRuleWithVariableInMessage() {
        MyRule r = new MyRule() {
            @Override
            public void apply(Node target, RuleContext ctx) {
                ctx.addViolation(target);
            }
        };
        r.definePropertyDescriptor(PropertyFactory.intProperty("testInt").desc("description").require(inRange(0, 100)).defaultValue(10).build());
        r.setMessage("Message ${packageName} ${className} ${methodName} ${variableName} ${testInt} ${noSuchProperty}");

        DummyRootNode s = helper.parse("abc()", "filename");

        RuleViolation rv = getReportForRuleApply(r, s).getViolations().get(0);
        assertEquals("Message foo ${className} ${methodName} ${variableName} 10 ${noSuchProperty}", rv.getDescription());
    }

    @Test
    void testRuleSuppress() {
        DummyRootNode n = helper.parse("abc()", "filename")
                                .withNoPmdComments(Collections.singletonMap(1, "ohio"));

        FileAnalysisListener listener = mock(FileAnalysisListener.class);
        RuleContext ctx = RuleContext.create(listener, new MyRule());
        ctx.addViolationWithMessage(n, "message");

        verify(listener, never()).onRuleViolation(any());
        verify(listener, times(1)).onSuppressedRuleViolation(any());
    }

    @Test
    void testEquals1() {
        MyRule r = new MyRule();
        assertFalse(r.equals(null), "A rule is never equals to null!");
    }

    @Test
    void testEquals2() {
        MyRule r = new MyRule();
        assertEquals(r, r, "A rule must be equals to itself");
    }

    @Test
    void testEquals3() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        assertEquals(r1, r2, "Two instances of the same rule are equal");
        assertEquals(r1.hashCode(), r2.hashCode(), "Hashcode for two instances of the same rule must be equal");
    }

    @Test
    void testEquals4() {
        MyRule myRule = new MyRule();
        assertFalse(myRule.equals("MyRule"), "A rule cannot be equal to an object of another class");
    }

    @Test
    void testEquals5() {
        MyRule myRule = new MyRule();
        MyOtherRule myOtherRule = new MyOtherRule();
        assertFalse(myRule.equals(myOtherRule), "Two rules from different classes cannot be equal");
    }

    @Test
    void testEquals6() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setName("MyRule2");
        assertFalse(r1.equals(r2), "Rules with different names cannot be equal");
    }

    @Test
    void testEquals7() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setPriority(RulePriority.HIGH);
        assertFalse(r1.equals(r2), "Rules with different priority levels cannot be equal");
    }

    @Test
    void testEquals8() {
        MyRule r1 = new MyRule();
        r1.setProperty(MyRule.XPATH_PROPERTY, "something");
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.XPATH_PROPERTY, "something else");
        assertFalse(r1.equals(r2), "Rules with different properties values cannot be equal");
    }

    @Test
    void testEquals9() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setProperty(MyRule.XPATH_PROPERTY, "something else");
        assertFalse(r1.equals(r2), "Rules with different properties cannot be equal");
    }

    @Test
    void testEquals10() {
        MyRule r1 = new MyRule();
        MyRule r2 = new MyRule();
        r2.setMessage("another message");
        assertEquals(r1, r2, "Rules with different messages are still equal");
        assertEquals(r1.hashCode(), r2.hashCode(), "Rules that are equal must have the an equal hashcode");
    }

    @Test
    void testDeepCopyRule() {
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
        assertEquals(r1.getRuleClass(), r2.getRuleClass());
        assertEquals(r1.getRuleSetName(), r2.getRuleSetName());
        assertEquals(r1.getSince(), r2.getSince());

        assertEquals(r1.isPropertyOverridden(MyRule.FOO_DEFAULT_PROPERTY),
                r2.isPropertyOverridden(MyRule.FOO_DEFAULT_PROPERTY));
    }
}
