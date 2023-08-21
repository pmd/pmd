/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.test.schema.RuleTestDescriptor;

class RuleTstTest {
    private final LanguageVersion dummyLanguage = DummyLanguageModule.getInstance().getDefaultVersion();

    private final Rule rule = mock(Rule.class);

    private final RuleTst ruleTester = spy(RuleTst.class);

    @Test
    void shouldCallStartAndEnd() {
        when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        when(rule.getName()).thenReturn("test rule");
        when(rule.getTargetSelector()).thenReturn(RuleTargetSelector.forRootOnly());
        when(rule.deepCopy()).thenReturn(rule);

        ruleTester.runTestFromString("the code", rule, dummyLanguage);

        verify(rule).initialize(any(LanguageProcessor.class));
        verify(rule).start(any(RuleContext.class));
        verify(rule).end(any(RuleContext.class));
        verify(rule, atLeastOnce()).getLanguage();
        verify(rule, atLeastOnce()).getTargetSelector();
        verify(rule, atLeastOnce()).getMinimumLanguageVersion();
        verify(rule, atLeastOnce()).getMaximumLanguageVersion();
        verify(rule).apply(any(Node.class), any(RuleContext.class));
        verify(rule, atLeastOnce()).getName();
        verify(rule).getPropertiesByPropertyDescriptor();
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
        testDescriptor.recordExpectedViolations(2, Arrays.asList(1, 2), Collections.emptyList());

        ruleTester.runTest(testDescriptor);
    }
}
