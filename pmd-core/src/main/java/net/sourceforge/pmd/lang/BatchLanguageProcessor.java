/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.processor.AbstractPMDProcessor;

/**
 * @author Clément Fournier
 */
public class BatchLanguageProcessor<P extends LanguagePropertyBundle> implements LanguageProcessor {

    private final Language language;
    private final P bundle;

    public BatchLanguageProcessor(Language language, P bundle) {
        this.language = language;
        this.bundle = bundle;
    }

    protected P getProperties() {
        return bundle;
    }

    @Override
    public LanguageVersionHandler services() {
        return bundle.getLanguageVersion().getLanguageVersionHandler();
    }

    @Override
    public final Language getLanguage() {
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
