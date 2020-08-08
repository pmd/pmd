/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.BaseResultProducingCloseable;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Listens to an analysis. This object produces new {@link FileAnalysisListener}
 * for each analysed file, which themselves handle events like violations,
 * in their file. Thread-safety is required.
 *
 * <p>Listeners are the main API to obtain results of an analysis. The
 * entry point of the API ({@link PMD#processFiles(PMDConfiguration, List, List, GlobalAnalysisListener) PMD::processFiles})
 * runs a set of rules on a set of files. What happens to events is entirely
 * the concern of the listener.
 *
 * <p>A useful kind of listener are the ones produced by {@linkplain Renderer#newListener() renderers}.
 * Another is the {@linkplain GlobalReportBuilderListener report builder}.
 */
public interface GlobalAnalysisListener extends AutoCloseable {

    /**
     * Returns a file listener that will handle events occurring during
     * the analysis of the given file. The new listener may receive events
     * as soon as this method returns. The analysis stops when the
     * {@link FileAnalysisListener#close()} method is called.
     *
     * @implSpec This routine may be called from several threads at once and
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
     * <p>Closing listeners multiple times should have no effect.
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

    static GlobalAnalysisListener noop() {
        return new GlobalAnalysisListener() {
            @Override
            public FileAnalysisListener startFileAnalysis(DataSource file) {
                return FileAnalysisListener.noop();
            }

            @Override
            public void close() {
                // do nothing
            }
        };
    }

    /**
     * Produce an analysis listener that forwards all events to the given
     * listeners.
     *
     * @param listeners Listeners
     *
     * @return A composed listener
     *
     * @throws IllegalArgumentException If the parameter is empty
     * @throws NullPointerException     If the parameter or any of its elements is null
     */
    static GlobalAnalysisListener tee(Collection<? extends GlobalAnalysisListener> listeners) {
        AssertionUtil.requireParamNotNull("Listeners", listeners);
        AssertionUtil.requireNotEmpty("Listeners", listeners);
        AssertionUtil.requireContainsNoNullValue("Listeners", listeners);

        if (listeners.size() == 1) {
            return listeners.iterator().next();
        }

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
