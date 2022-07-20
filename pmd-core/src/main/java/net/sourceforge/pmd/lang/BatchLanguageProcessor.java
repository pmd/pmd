/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.processor.AbstractPMDProcessor;

/**
 * @author Clément Fournier
 */
public class BatchLanguageProcessor implements LanguageProcessor {

    private final Language language;
    private final LanguagePropertyBundle bundle;

    public BatchLanguageProcessor(Language language, LanguagePropertyBundle bundle) {
        this.language = language;
        this.bundle = bundle;
    }

    @Override
    public LanguageVersionHandler services() {
        return bundle.getLanguageVersion().getLanguageVersionHandler();
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public AutoCloseable launchAnalysis(AnalysisTask analysisTask) {
        AbstractPMDProcessor processor = AbstractPMDProcessor.newFileProcessor(
            analysisTask,
            this
        );
        processor.processFiles();
        return processor;
    }

    @Override
    public void close() throws Exception {
        // no additional resources
    }
}
