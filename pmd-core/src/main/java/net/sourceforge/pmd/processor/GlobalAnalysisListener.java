/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 *
 */
public interface GlobalAnalysisListener extends AutoCloseable {

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
