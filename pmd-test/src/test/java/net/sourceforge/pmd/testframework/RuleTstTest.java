/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.test.lang.DummyLanguageModule;
import net.sourceforge.pmd.test.lang.DummyLanguageModule.DummyRootNode;
import net.sourceforge.pmd.test.schema.RuleTestDescriptor;

class RuleTstTest {
    private LanguageVersion dummyLanguage = DummyLanguageModule.getInstance().getDefaultVersion();

    private Rule rule = mock(Rule.class);

    private RuleTst ruleTester = new RuleTst() {
        @Override
        public Rule findRule(String ruleSet, String ruleName) {
            return rule;
        }
    };

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

        final String code = "the\ncode";
        Mockito.doAnswer(invocation -> {
            RuleContext context = invocation.getArgument(1, RuleContext.class);
            DummyRootNode node = invocation.getArgument(0, DummyRootNode.class);

            // the violations are reported out of order
            // line 2
            context.addViolation(node.newChild().withCoords(TextRegion.fromOffsetLength("the\n".length(), "code".length())));
            // line 1
            context.addViolation(node.newChild().withCoords(TextRegion.fromOffsetLength(0, "the".length())));
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
