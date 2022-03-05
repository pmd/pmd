/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.hamcrest.Matchers.empty;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

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

}
