/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import static org.mockito.ArgumentMatchers.any;
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
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.test.lang.ast.DummyNode;

public class RuleTstTest {
    private LanguageVersion dummyLanguage = LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion();

    private Rule rule = mock(Rule.class);

    private RuleTst ruleTester = new RuleTst() {
    };

    @Test
    public void shouldCallStartAndEnd() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());

        Report report = ruleTester.runTestFromString("the code", rule, dummyLanguage, false);

        verify(rule).start(any(RuleContext.class));
        verify(rule).end(any(RuleContext.class));
        verify(rule).getLanguage();
        verify(rule, times(2)).getTargetSelector();
        verify(rule).getMinimumLanguageVersion();
        verify(rule).getMaximumLanguageVersion();
        verify(rule).apply(any(Node.class), any(RuleContext.class));
        verify(rule, times(4)).getName();
        verify(rule).getPropertiesByPropertyDescriptor();
        verifyNoMoreInteractions(rule);
    }

    @Test
    public void shouldAssertLinenumbersSorted() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());

        Mockito.doAnswer(new Answer<Void>() {
            private RuleViolation createViolation(int beginLine, String message) {
                DummyNode node = new DummyNode();
                node.setCoords(beginLine, 1, beginLine + 1, 2);
                return new ParametricRuleViolation<Node>(rule, "someFile", node, message);
            }

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RuleContext context = invocation.getArgument(1, RuleContext.class);
                // the violations are reported out of order
                context.addViolationNoSuppress(createViolation(15, "first reported violation"));
                context.addViolationNoSuppress(createViolation(5, "second reported violation"));
                return null;
            }
        }).when(rule).apply(any(Node.class), Mockito.any(RuleContext.class));

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
