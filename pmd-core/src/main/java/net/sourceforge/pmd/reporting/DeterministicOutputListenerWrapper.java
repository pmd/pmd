/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.Report.ReportBuilderListener;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;

/**
 * A listener that mediates access to another listener to order events
 * in a predetermined, stable way. When running PMD with multiple threads,
 * file listeners may be called in any order. This makes runs non-deterministic,
 * as Renderers can output reports in the particular order files were processed,
 * which varies between runs. This class will reorder the events by buffering
 * them, to call the methods of the underlying GlobalAnalysisListener in a
 * deterministic order.
 *
 * <p>Any renderer whose output may depend on ordering of files should be shielded
 * by an instance of this wrapper. Note that each wrapper maintains its own buffer
 * so it should be
 */
public class DeterministicOutputListenerWrapper implements GlobalAnalysisListener {
    private static final Logger LOG = LoggerFactory.getLogger(DeterministicOutputListenerWrapper.class.getName());

    private final GlobalAnalysisListener listener;
    private final Map<FileId, Integer> filesToIdx = new HashMap<>();

    // use linkedlist because we are mostly doing one-element insertions and removals
    private final List<ReportWrapper> reportBuffer = new LinkedList<>();
    private int nextToOutput;
    private final Object lock = new Object();


    public DeterministicOutputListenerWrapper(GlobalAnalysisListener listener) {
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    public ListenerInitializer initializer() {
        return ListenerInitializer.tee(listOf(new ListenerInitializer() {
            @Override
            public void setSortedFileIds(List<FileId> files) {
                for (int i = 0; i < files.size(); i++) {
                    filesToIdx.put(files.get(i), i);
                }
            }
        }, listener.initializer()));
    }

    @Override
    public FileAnalysisListener startFileAnalysis(TextFile file) {
        Integer fileIdx = filesToIdx.get(file.getFileId());
        Objects.requireNonNull(fileIdx, "File " + file.getFileId() + " was not declared when starting the analysis");

        return new CloseHookFileListener<ReportBuilderListener>(new ReportBuilderListener()) {
            @Override
            protected void doClose(ReportBuilderListener delegate, @Nullable Exception ignored) throws Exception {
                Report result = delegate.getResult();
                ReportWrapper wrapper = new ReportWrapper(result, file, fileIdx);
                synchronized (lock) {
                    if (fileIdx == nextToOutput) {
                        outputReport(wrapper);
                        nextToOutput++;
                        // bumping the "next to output" index might make buffered reports flushable
                        tryToFlushBuffer();
                    } else {
                        // if we shouldn't output this report yet, insert it in sorted order for later
                        ListIterator<ReportWrapper> iter = reportBuffer.listIterator();
                        // find the correct insertion point
                        while (iter.hasNext()) {
                            ReportWrapper w = iter.next();
                            if (w.compareTo(wrapper) > 0) {
                                // wrapper sorts higher than list element w,
                                // backup before that list element
                                iter.previous();
                                break;
                            }
                        }
                        iter.add(wrapper);
                    }
                }
            }
        };
    }

    private void tryToFlushBuffer() throws Exception {
        int lastOutput = this.nextToOutput;
        ListIterator<ReportWrapper> iter = reportBuffer.listIterator();
        while (iter.hasNext()) {
            ReportWrapper next = iter.next();
            if (next.idx == nextToOutput) {
                iter.remove();
                outputReport(next);
                nextToOutput++;
            } else {
                break;
            }
        }
        int numOutput = this.nextToOutput - lastOutput;
        if (numOutput > 0) {
            LOG.trace("Flushed {} out of {} buffered reports", numOutput, reportBuffer.size() + numOutput);
        }
    }

    @Override
    public void close() throws Exception {
        synchronized (lock) {
            tryToFlushBuffer();
            if (!reportBuffer.isEmpty()) {
                // this would be a problem in PmdAnalysis, maybe because it didn't join on the parallel processing tasks.
                throw new AssertionError("Closed listener but not all files have been processed");
            }
        }
        listener.close();
    }

    @Override
    public void onConfigError(ConfigurationError error) {
        listener.onConfigError(error);
    }

    private void outputReport(ReportWrapper wrapper) throws Exception {
        Report report = wrapper.report;
        try (FileAnalysisListener fileListener = listener.startFileAnalysis(wrapper.textFile)) {
            for (RuleViolation v : report.getViolations()) {
                fileListener.onRuleViolation(v);
            }
            for (SuppressedViolation sv : report.getSuppressedViolations()) {
                fileListener.onSuppressedRuleViolation(sv);
            }
            for (ProcessingError error : report.getProcessingErrors()) {
                fileListener.onError(error);
            }
        }
    }

    @Override
    public String toString() {
        return "DeterministicOutputListenerWrapper [listener=" + listener + ", bufferSize=" + reportBuffer.size() + "]";
    }

    private static final class ReportWrapper implements Comparable<ReportWrapper> {
        private final Report report;
        private final TextFile textFile;
        private final int idx;

        ReportWrapper(Report report, TextFile textFile, int idx) {
            this.report = report;
            this.textFile = textFile;
            this.idx = idx;
        }

        @Override
        public int compareTo(ReportWrapper o) {
            return Integer.compare(this.idx, o.idx);
        }
    }
}
