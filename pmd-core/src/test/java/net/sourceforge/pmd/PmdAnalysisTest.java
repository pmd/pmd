/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import net.sourceforge.pmd.renderers.Renderer;

/**
 * @author Clément Fournier
 */
public class PmdAnalysisTest {

    @Test
    public void testPmdAnalysisWithEmptyConfig() {
        PMDConfiguration config = new PMDConfiguration();
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            MatcherAssert.assertThat(pmd.files().getCollectedFiles(), empty());
            MatcherAssert.assertThat(pmd.rulesets(), empty());
            MatcherAssert.assertThat(pmd.renderers(), empty());
        }
    }

    @Test
    public void testRendererStart() throws IOException {
        PMDConfiguration config = new PMDConfiguration();
        config.setInputPaths("sample-source/dummy");
        Renderer renderer = mock(Renderer.class);
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

}
