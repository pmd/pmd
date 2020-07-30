/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.util.BaseResultProducingCloseable;
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
     * Returns a file listener that will handle events occurring during
     * the analysis of the given file. The new listener may receive events
     * as soon as this method returns. The analysis stops when the
     * {@link FileAnalysisListener#close()} method is called.
     *
     * <p>This routine may be called from several threads at once and
     * needs to be thread-safe. But the returned listener will only be
     * used in a single thread.
     *
     * @param file File to be processed
     *
     * @return A new listener
     *
     * @throws IllegalStateException If {@link #close()} has already been called.
     *                               This prevents manipulation mistakes but is
     *                               not a strong requirement.
     */
    FileAnalysisListener startFileAnalysis(DataSource file);

    /**
     * Notify the implementation that the analysis ended, ie all files
     * have been processed. This listener won't be used after this is
     * called.
     *
     * <p>Closing listeners multiple times should have no effect
     *
     * @throws Exception If an error occurs. For example, renderer listeners
     *                   may throw {@link IOException}
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
     * @return A composed listener
     */
    static GlobalAnalysisListener tee(List<? extends GlobalAnalysisListener> listeners) {
        final class TeeListener implements GlobalAnalysisListener {

            final List<GlobalAnalysisListener> myList;

            TeeListener(List<GlobalAnalysisListener> myList) {
                this.myList = myList;
            }
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

            @Override
            public String toString() {
                return "TeeListener{" + myList + '}';
            }
        }

        // Flatten other tee listeners in the list
        // This prevents suppressed exceptions from being chained too deep if they occur in close()
        List<GlobalAnalysisListener> myList =
            listeners.stream()
                     .flatMap(l -> l instanceof TeeListener ? ((TeeListener) l).myList.stream() : Stream.of(l))
                     .collect(CollectionUtil.toUnmodifiableList());


        return new TeeListener(myList);
    }


    /**
     * A listener that just counts recorded violations. The result is
     * available after the listener is closed ({@link #getResult()}).
     */
    final class ViolationCounterListener extends BaseResultProducingCloseable<Integer> implements GlobalAnalysisListener {

        private final AtomicInteger count = new AtomicInteger();

        @Override
        protected Integer getResultImpl() {
            return count.get();
        }

        @Override
        public FileAnalysisListener startFileAnalysis(DataSource file) {
            return violation -> count.incrementAndGet();
        }
    }


    /**
     * A listener that throws processing errors when they occur. They
     * are all thrown as {@link FileAnalysisException}s. Config errors
     * are ignored.
     *
     * <p>This will abort the analysis on the first error, which is
     * usually not what you want to do, but is useful for unit tests.
     */
    static GlobalAnalysisListener exceptionThrower() {
        class ExceptionThrowingListener implements GlobalAnalysisListener {

            @Override
            public FileAnalysisListener startFileAnalysis(DataSource file) {
                String filename = file.getNiceFileName(false, null);
                return new FileAnalysisListener() {
                    @Override
                    public void onRuleViolation(RuleViolation violation) {
                        // do nothing
                    }

                    @Override
                    public void onError(ProcessingError error) throws FileAnalysisException {
                        throw FileAnalysisException.wrap(filename, "Unknown error", error.getError());
                    }
                };
            }

            @Override
            public void close() {
                // nothing to do
            }
        }

        return new ExceptionThrowingListener();
    }

}
