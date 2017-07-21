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

import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

public class RuleTstTest {

    @Test
    public void shouldCallStartAndEnd() {
        RuleTst ruleTester = new RuleTst() {
        };
        LanguageVersion languageVersion = LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion();
        Report report = new Report();
        Rule rule = mock(Rule.class);
        when(rule.getLanguage()).thenReturn(languageVersion.getLanguage());
        when(rule.getName()).thenReturn("test rule");

        ruleTester.runTestFromString("the code", rule, report, languageVersion, false);

        verify(rule).start(any(RuleContext.class));
        verify(rule).end(any(RuleContext.class));
        verify(rule, times(5)).getLanguage();
        verify(rule).usesDFA();
        verify(rule).usesTypeResolution();
        verify(rule).usesMetrics();
        verify(rule, times(2)).usesRuleChain();
        verify(rule).getMinimumLanguageVersion();
        verify(rule).getMaximumLanguageVersion();
        verify(rule).apply(anyList(), any(RuleContext.class));
        verify(rule, times(4)).getName();
        verify(rule).getPropertiesByPropertyDescriptor();
        verifyNoMoreInteractions(rule);
    }
}
