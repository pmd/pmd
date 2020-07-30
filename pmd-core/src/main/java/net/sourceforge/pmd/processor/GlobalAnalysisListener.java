/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Listens to an analysis for file events. This object should be thread-safe.
 * It produces new {@link FileAnalysisListener} for each analysed file.
 *
 * <p>Listeners are assumed to be ready to receive events as soon as they
 * are constructed.
 */
public interface GlobalAnalysisListener extends AutoCloseable {

    /**
     * Start the analysis of the given file. The analysis stops
     * when the {@link FileAnalysisListener#close()} method is called.
     *
     * <p>This routine may be called from several threads at once and
     * needs to be thread-safe.
     *
     * @param file File to be processed
     *
     * @return A new listener
     */
    FileAnalysisListener startFileAnalysis(DataSource file);

    /**
     * Notify the implementation that the analysis ended, ie all files
     * have been processed.
     */
    @Override
    void close() throws Exception;


    /**
     * Record a configuration error. This happens before the start of
     * file analysis.
     */
    default void onConfigError(ConfigurationError error) {
        // do nothing
    }


    /**
     * Produce an analysis listener that forwards all events to the given
     * listeners.
     *
     * @param listeners Listeners
     *
     * @return A new listener
     */
    static GlobalAnalysisListener tee(List<? extends GlobalAnalysisListener> listeners) {
        List<GlobalAnalysisListener> myList = Collections.unmodifiableList(new ArrayList<>(listeners));
        return new GlobalAnalysisListener() {
            @Override
            public FileAnalysisListener startFileAnalysis(DataSource file) {
                return FileAnalysisListener.tee(CollectionUtil.map(myList, it -> it.startFileAnalysis(file)));
            }

            @Override
            public void close() throws Exception {
                Exception composed = FileUtil.closeAll(myList);
                if (composed != null) {
                    throw composed;
                }
            }
        };
    }

    /**
     * A listener that just counts recorded violations.
     */
    final class ViolationCounterListener implements GlobalAnalysisListener, FileAnalysisListener {

        private final AtomicInteger count = new AtomicInteger();


        /**
         * Get the number of violations recorded.
         */
        public int getCount() {
            return count.get();
        }

        @Override
        public FileAnalysisListener startFileAnalysis(DataSource file) {
            return this;
        }

        @Override
        public void onRuleViolation(RuleViolation violation) {
            count.incrementAndGet();
        }

        @Override
        public void close() {
            // nothing to do
        }
    }


    /**
     * A listener that throws processing errors when they occur. They
     * are all thrown as {@link FileAnalysisException}s. Config errors
     * are ignored.
     */
    static GlobalAnalysisListener exceptionThrower() {
        return new GlobalAnalysisListener() {
            @Override
            public FileAnalysisListener startFileAnalysis(DataSource file) {
                return new FileAnalysisListener() {
                    @Override
                    public void onRuleViolation(RuleViolation violation) {
                        // do nothing
                    }

                    @Override
                    public void onError(ProcessingError error) throws FileAnalysisException {
                        Throwable cause = error.getError();
                        if (cause instanceof FileAnalysisException) {
                            throw (FileAnalysisException) cause;
                        }
                        throw new FileAnalysisException(cause);
                    }
                };
            }

            @Override
            public void close() {
                // nothing to do
            }
        };
    }


}
