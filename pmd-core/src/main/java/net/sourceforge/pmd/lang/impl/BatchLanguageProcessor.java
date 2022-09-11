/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.TextFile;

/**
 * A base class for language processors. It processes all files of the
 * corresponding language as a single batch. It can operate in parallel
 * or sequentially depending on the number of threads passed in the
 * {@link AnalysisTask}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class BatchLanguageProcessor<P extends LanguagePropertyBundle> implements LanguageProcessor {

    private final Language language;
    private final P bundle;
    private final LanguageVersion version;

    protected BatchLanguageProcessor(P bundle) {
        this.language = bundle.getLanguage();
        this.bundle = bundle;
        this.version = bundle.getLanguageVersion();
    }

    public P getProperties() {
        return bundle;
    }

    @Override
    public @NonNull LanguageVersion getLanguageVersion() {
        return version;
    }

    @Override
    public final @NonNull Language getLanguage() {
        return language;
    }

    @Override
    public @NonNull AutoCloseable launchAnalysis(@NonNull AnalysisTask task) {
        // The given analysis task has all files to analyse, not only the ones for this language.
        List<TextFile> files = new ArrayList<>(task.getFiles());
        files.removeIf(it -> !it.getLanguageVersion().getLanguage().equals(getLanguage()));
        AnalysisTask newTask = task.withFiles(files);

        task.getRulesets().initializeRules(task.getLpRegistry(), task.getMessageReporter());

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
