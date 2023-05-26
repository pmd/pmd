/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageProcessor;

class AbstractPMDProcessorTest {
    @Test
    void shouldUseMonoThreadProcessorForZeroOnly() {
        AbstractPMDProcessor processor = AbstractPMDProcessor.newFileProcessor(createTask(0));
        assertSame(MonoThreadProcessor.class, processor.getClass());

        processor = AbstractPMDProcessor.newFileProcessor(createTask(1));
        assertSame(MultiThreadProcessor.class, processor.getClass());
    }

    private LanguageProcessor.AnalysisTask createTask(int threads) {
        LanguageProcessor.AnalysisTask task = new LanguageProcessor.AnalysisTask(null, null, null, threads, null, null, null);
        return task;
    }
}
