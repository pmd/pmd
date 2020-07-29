/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * A handler for analysis events. This must be thread safe.
 */
public interface ThreadSafeAnalysisListener extends AutoCloseable {


    interface GlobalAnalysisListener extends AutoCloseable {

        ThreadSafeAnalysisListener startFileAnalysis(DataSource file);


        static GlobalAnalysisListener tee(List<? extends GlobalAnalysisListener> list) {
            List<GlobalAnalysisListener> listeners = Collections.unmodifiableList(new ArrayList<>(list));
            return new GlobalAnalysisListener() {
                @Override
                public ThreadSafeAnalysisListener startFileAnalysis(DataSource file) {
                    return ThreadSafeAnalysisListener.tee(CollectionUtil.map(listeners, it -> it.startFileAnalysis(file)));
                }

                @Override
                public void close() throws Exception {
                    Exception composed = null;
                    for (GlobalAnalysisListener it : list) {
                        try {
                            it.close();
                        } catch (Exception e) {
                            if (composed == null) {
                                composed = e;
                            } else {
                                composed.addSuppressed(e);
                            }
                        }
                    }
                    if (composed != null) {
                        throw composed;
                    }
                }
            };
        }


        static GlobalAnalysisListener forReporter(Renderer renderer) {

            return new GlobalAnalysisListener() {
                @Override
                public ThreadSafeAnalysisListener startFileAnalysis(DataSource file) {
                    renderer.startFileAnalysis(file);
                    return new ThreadSafeAnalysisListener() {
                        final ReportBuilderListener reportBuilder = new ReportBuilderListener();

                        @Override
                        public void onRuleViolation(RuleViolation violation) {
                            reportBuilder.onRuleViolation(violation);
                        }

                        @Override
                        public void onSuppressedRuleViolation(SuppressedViolation violation) {
                            reportBuilder.onSuppressedRuleViolation(violation);
                        }

                        @Override
                        public void onError(ProcessingError error) {
                            reportBuilder.onError(error);
                        }

                        @Override
                        public void close() throws Exception {
                            reportBuilder.close();
                            renderer.renderFileReport(reportBuilder.getReport());
                        }
                    };
                }

                @Override
                public void close() throws Exception {
                    try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
                        renderer.end();
                        renderer.flush();
                    }
                }
            };
        }
    }


    /**
     * Handle a new violation (not suppressed).
     */
    default void onRuleViolation(RuleViolation violation) {

    }


    /**
     * Handle a new suppressed violation.
     */
    default void onSuppressedRuleViolation(SuppressedViolation violation) {

    }


    /**
     * Handle an error that occurred while processing a file.
     */
    default void onError(ProcessingError error) {

    }


    @Override
    default void close() throws Exception {

    }


    static ThreadSafeAnalysisListener noop() {
        return new ThreadSafeAnalysisListener() {};
    }


    static ThreadSafeAnalysisListener tee(Collection<? extends ThreadSafeAnalysisListener> listeners) {
        List<ThreadSafeAnalysisListener> list = Collections.unmodifiableList(new ArrayList<>(listeners));
        return new ThreadSafeAnalysisListener() {
            @Override
            public void onRuleViolation(RuleViolation violation) {
                for (ThreadSafeAnalysisListener it : list) {
                    it.onRuleViolation(violation);
                }
            }

            @Override
            public void onSuppressedRuleViolation(SuppressedViolation violation) {
                for (ThreadSafeAnalysisListener it : list) {
                    it.onSuppressedRuleViolation(violation);
                }
            }

            @Override
            public void onError(ProcessingError error) {
                for (ThreadSafeAnalysisListener it : list) {
                    it.onError(error);
                }
            }

            @Override
            public void close() throws Exception {
                Exception composed = null;
                for (ThreadSafeAnalysisListener it : list) {
                    try {
                        it.close();
                    } catch (Exception e) {
                        if (composed == null) {
                            composed = e;
                        } else {
                            composed.addSuppressed(e);
                        }
                    }
                }
                if (composed != null) {
                    throw composed;
                }
            }
        };

    }

}
