/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import net.sourceforge.pmd.lang.document.TextFile;

/**
 * @author Clément Fournier
 */
final class NoopAnalysisListener implements GlobalAnalysisListener {

    static final NoopAnalysisListener INSTANCE = new NoopAnalysisListener();

    private NoopAnalysisListener() {

    }

    @Override
    public FileAnalysisListener startFileAnalysis(TextFile file) {
        return FileAnalysisListener.noop();
    }

    @Override
    public void close() {
        // do nothing
    }
}
