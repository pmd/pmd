/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.SuppressionCommentImpl;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.test.schema.RuleTestDescriptor;

class RuleTstTest {
    private final LanguageVersion dummyLanguage = DummyLanguageModule.getInstance().getDefaultVersion();

    private Rule rule = spy(AbstractRule.class);

    private final RuleTst ruleTester = spy(RuleTst.class);

    @Test
    void shouldCallStartAndEnd() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());
        when(rule.deepCopy()).thenReturn(rule);

        ruleTester.runTestFromString("the code", rule, dummyLanguage);

        verify(rule, atLeastOnce()).initialize(any(LanguageProcessor.class));
        verify(rule).start(any(RuleContext.class));
        verify(rule).apply(any(Node.class), any(RuleContext.class));
        verify(rule).end(any(RuleContext.class));
    }

    @Test
    void shouldAssertLinenumbersSorted() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getMessage()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());
        when(rule.deepCopy()).thenReturn(rule);

        final String code = "(a)(b)\n(c)";
        Mockito.doAnswer(invocation -> {
            RuleContext context = invocation.getArgument(1, RuleContext.class);
            DummyRootNode node = invocation.getArgument(0, DummyRootNode.class);

            assertEquals(3, node.getNumChildren());
            // the violations are reported out of order
            // line 2
            context.addViolation(node.getChild(2));
            // line 1
            context.addViolation(node.getChild(1));
            return null;
        }).when(rule).apply(any(Node.class), Mockito.any(RuleContext.class));

        RuleTestDescriptor testDescriptor = new RuleTestDescriptor(0, rule);
        testDescriptor.setLanguageVersion(dummyLanguage);
        testDescriptor.setCode(code);
        testDescriptor.setDescription("sample test");
        testDescriptor.recordExpectedViolations(2, Arrays.asList(1, 2), Arrays.asList(1, 2), Collections.emptyList());

        ruleTester.runTest(testDescriptor);
    }

    private void setupRuleWithSuppression() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getMessage()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());
        when(rule.deepCopy()).thenReturn(rule);

        Mockito.doAnswer(invocation -> {
            RuleContext context = invocation.getArgument(1, RuleContext.class);
            DummyRootNode node = invocation.getArgument(0, DummyRootNode.class);

            node = node.withNoPmdComments(new SuppressionCommentImpl<>(node, "ohio"));
            context.addViolation(node);

            return null;
        }).when(rule).apply(any(Node.class), Mockito.any(RuleContext.class));
    }

    @Test
    void suppressionAssertSuppressorIdIsOptional() {
        setupRuleWithSuppression();

        RuleTestDescriptor testDescriptor = new RuleTestDescriptor(0, rule);
        testDescriptor.setLanguageVersion(dummyLanguage);
        testDescriptor.setCode("(a)(b)\n(c)");
        testDescriptor.setDescription("sample test");
        testDescriptor.recordExpectedViolations(0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        testDescriptor.recordExpectedSuppression(1);

        ruleTester.runTest(testDescriptor);
    }

    @Test
    void suppressionAssertSuppressorIdGood() {
        setupRuleWithSuppression();

        RuleTestDescriptor testDescriptor = new RuleTestDescriptor(0, rule);
        testDescriptor.setLanguageVersion(dummyLanguage);
        testDescriptor.setCode("(a)(b)\n(c)");
        testDescriptor.setDescription("sample test");
        testDescriptor.recordExpectedViolations(0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        testDescriptor.recordExpectedSuppression(1, "//NOPMD");

        ruleTester.runTest(testDescriptor);
    }

    @Test
    void suppressionAssertSuppressorIdWrong() {
        setupRuleWithSuppression();

        RuleTestDescriptor testDescriptor = new RuleTestDescriptor(0, rule);
        testDescriptor.setLanguageVersion(dummyLanguage);
        testDescriptor.setCode("(a)(b)\n(c)");
        testDescriptor.setDescription("sample test");
        testDescriptor.recordExpectedViolations(0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        testDescriptor.recordExpectedSuppression(1, "wrong id");

        AssertionFailedError assertionFailedError = assertThrows(AssertionFailedError.class, () -> ruleTester.runTest(testDescriptor));
        assertEquals("wrong suppressor id ==> expected: <wrong id> but was: <//NOPMD>", assertionFailedError.getMessage());
    }
}
