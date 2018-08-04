/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.test.lang.ast.DummyNode;

public class RuleTstTest {
    private LanguageVersion dummyLanguage = LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion();

    private Rule rule = mock(Rule.class);

    private RuleTst ruleTester = new RuleTst() {
    };

    @Test
    public void shouldCallStartAndEnd() {
        Report report = new Report();
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");

        ruleTester.runTestFromString("the code", rule, report, dummyLanguage, false);

        verify(rule).start(any(RuleContext.class));
        verify(rule).end(any(RuleContext.class));
        verify(rule, times(5)).getLanguage();
        verify(rule).isDfa();
        verify(rule).isTypeResolution();
        verify(rule).isMultifile();
        verify(rule, times(2)).isRuleChain();
        verify(rule).getMinimumLanguageVersion();
        verify(rule).getMaximumLanguageVersion();
        verify(rule).apply(anyList(), any(RuleContext.class));
        verify(rule, times(4)).getName();
        verify(rule).getPropertiesByPropertyDescriptor();
        verifyNoMoreInteractions(rule);
    }

    @Test
    public void shouldAssertLinenumbersSorted() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        Mockito.doAnswer(new Answer<Void>() {
            private RuleViolation createViolation(RuleContext context, int beginLine, String message) {
                DummyNode node = new DummyNode(1);
                node.testingOnlySetBeginLine(beginLine);
                node.testingOnlySetBeginColumn(1);
                ParametricRuleViolation<Node> violation = new ParametricRuleViolation<Node>(rule, context, node, message);
                return violation;
            }

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RuleContext context = invocation.getArgumentAt(1, RuleContext.class);
                // the violations are reported out of order
                context.getReport().addRuleViolation(createViolation(context, 15, "first reported violation"));
                context.getReport().addRuleViolation(createViolation(context, 5, "second reported violation"));
                return null;
            }
        }).when(rule).apply(Mockito.anyList(), Mockito.any(RuleContext.class));

        TestDescriptor testDescriptor = new TestDescriptor("the code", "sample test", 2, rule, dummyLanguage);
        testDescriptor.setReinitializeRule(false);
        testDescriptor.setExpectedLineNumbers(Arrays.asList(5, 15));

        try {
            ruleTester.runTest(testDescriptor);
            // there should be no assertion failures
            // expected line numbers and actual line numbers match
        } catch (AssertionError assertionError) {
            Assert.fail(assertionError.toString());
        }
    }
}
