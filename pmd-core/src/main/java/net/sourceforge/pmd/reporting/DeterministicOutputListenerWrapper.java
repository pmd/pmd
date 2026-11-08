/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
 *
 * @since 7.12.0
 */
public class DeterministicOutputListenerWrapper implements GlobalAnalysisListener {
    private static final Logger LOG = LoggerFactory.getLogger(DeterministicOutputListenerWrapper.class.getName());

    /** Maximum number of file names per group in the error message of {@link #close()}. */
    private static final int MAX_FILES_IN_MESSAGE = 10;

    private final GlobalAnalysisListener listener;
    private final Map<FileId, Integer> filesToIdx = new HashMap<>();

    // use linkedlist because we are mostly doing one-element insertions and removals
    private final List<ReportWrapper> reportBuffer = new LinkedList<>();
    private final Object lock = new Object();
    private int nextToOutput;


    public DeterministicOutputListenerWrapper(GlobalAnalysisListener listener) {
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    public ListenerInitializer initializer() {
        return ListenerInitializer.tee(listOf(new ListenerInitializer() {
            @Override
            public void setFilesToAnalyze(List<FileId> files) {
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
                // This would be a problem in PmdAnalysis, maybe because it didn't join
                // on the parallel processing tasks, e.g. because an Error in one analysis
                // thread aborted the analysis while other files were still running.
                // Report which files are missing, so that this error gives a hint at
                // the real cause.
                throw new AssertionError("Closed listener but not all files have been processed:\n"
                                             + unprocessedFiles());
            }
        }
        listener.close();
    }

    /**
     * Builds the details for the error thrown by {@link #close()} when not all
     * declared files have been output. The files are split into two groups:
     * <ul>
     *     <li>files that never delivered a report: their analysis was still
     *         running or was never started</li>
     *     <li>files that were analyzed, but whose reports are still buffered
     *         because they are waiting for one of the files above</li>
     * </ul>
     * Each group shows its size and the first {@value #MAX_FILES_IN_MESSAGE}
     * file names (sorted), followed by the number of files left out.
     *
     * <p>Must be called while holding {@link #lock}.
     *
     * @return the two groups, one per line, each indented by two spaces
     */
    private String unprocessedFiles() {
        Set<Integer> bufferedIdx = reportBuffer.stream()
                .map(w -> w.idx)
                .collect(Collectors.toSet());
        List<String> notProcessed = new ArrayList<>();
        for (Map.Entry<FileId, Integer> entry : filesToIdx.entrySet()) {
            int idx = entry.getValue();
            if (idx >= nextToOutput && !bufferedIdx.contains(idx)) {
                notProcessed.add(entry.getKey().getOriginalPath());
            }
        }

        List<String> buffered = reportBuffer.stream()
                .map(w -> w.textFile.getFileId().getOriginalPath())
                .collect(Collectors.toList());
        notProcessed.sort(null);
        buffered.sort(null);

        return "  " + describeGroup("never delivered a report", notProcessed)
            + "\n  " + describeGroup("analyzed but still buffered", buffered);
    }

    /**
     * Formats one group of {@link #unprocessedFiles()}, e.g.
     * {@code "13 file(s) never delivered a report: [a, b, ...] ... and 3 more"}.
     * Only the first {@value #MAX_FILES_IN_MESSAGE} file names are listed.
     */
    private static String describeGroup(String label, List<String> files) {
        String message = files.size() + " file(s) " + label + ": ";
        if (files.size() <= MAX_FILES_IN_MESSAGE) {
            return message + files;
        }
        return message + files.subList(0, MAX_FILES_IN_MESSAGE)
            + " ... and " + (files.size() - MAX_FILES_IN_MESSAGE) + " more";
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ReportWrapper that = (ReportWrapper) o;
            return idx == that.idx && Objects.equals(report, that.report) && Objects.equals(textFile, that.textFile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(report, textFile, idx);
        }
    }
}
