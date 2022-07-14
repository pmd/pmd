/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;
import org.mockito.ArgumentMatchers;

import net.sourceforge.pmd.RuleSetTest.MockRule;
import net.sourceforge.pmd.processor.PmdRunnableTest;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.reporting.ReportStats;

/**
 * @author Clément Fournier
 */
public class PmdAnalysisTest {

    @Test
    public void testPmdAnalysisWithEmptyConfig() {
        PMDConfiguration config = new PMDConfiguration();
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            assertThat(pmd.files().getCollectedFiles(), empty());
            assertThat(pmd.rulesets(), empty());
            assertThat(pmd.renderers(), empty());
        }
    }

    @Test
    public void testRendererInteractions() throws IOException {
        PMDConfiguration config = new PMDConfiguration();
        config.setInputPaths("sample-source/dummy");
        Renderer renderer = spy(Renderer.class);
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRenderer(renderer);
            verify(renderer, never()).start();
            pmd.performAnalysis();
        }

        verify(renderer, times(1)).renderFileReport(ArgumentMatchers.<Report>any());
        verify(renderer, times(1)).start();
        verify(renderer, times(1)).end();
        verify(renderer, times(1)).flush();
    }

    @Test
    public void testRulesetLoading() {
        PMDConfiguration config = new PMDConfiguration();
        config.addRuleSet("rulesets/dummy/basic.xml");
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            assertThat(pmd.rulesets(), hasSize(1));
        }
    }

    @Test
    public void testRulesetWhenSomeoneHasAnError() {
        PMDConfiguration config = new PMDConfiguration();
        config.addRuleSet("rulesets/dummy/basic.xml");
        config.addRuleSet("rulesets/xxxe/notaruleset.xml");
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            assertThat(pmd.rulesets(), hasSize(1)); // no failure
            assertThat(pmd.getReporter().numErrors(), equalTo(1));
        }
    }

    @Test
    public void testParseException() {
        PMDConfiguration config = new PMDConfiguration();
        config.setThreads(1);
        config.setForceLanguageVersion(PmdRunnableTest.getVersionWithParserThatThrowsSemanticError());
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(new MockRule()));
            pmd.files().addSourceFile("file", "some source");

            ReportStats stats = pmd.runAndReturnStats();
            assertEquals("Errors", 1, stats.getNumErrors());
            assertEquals("Violations", 0, stats.getNumViolations());
        }
    }
}
