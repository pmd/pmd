/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.document.TextFile;

/**
 * @author Clément Fournier
 */
public class BatchLanguageProcessor<P extends LanguagePropertyBundle> implements LanguageProcessor {

    private final Language language;
    private final P bundle;

    protected BatchLanguageProcessor(P bundle) {
        this.language = bundle.getLanguage();
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
        // The given analysis task has all files to analyse, not only the ones for this language.
        List<TextFile> files = new ArrayList<>(analysisTask.getFiles());
        files.removeIf(it -> !it.getLanguageVersion().getLanguage().equals(getLanguage()));
        AnalysisTask newTask = analysisTask.withFiles(files);

        // launch processing.
        AbstractPMDProcessor processor = AbstractPMDProcessor.newFileProcessor(newTask);
        // If this is a multi-threaded processor, this call is non-blocking,
        // the call to close on the returned instance blocks instead.
        processor.processFiles();
        return processor;
    }

    @Override
    public void close() throws Exception {
        // no additional resources
    }
}
