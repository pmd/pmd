/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.benchmark.TimeTracker.TimedResult;

/**
 * A text based renderer for {@link TimingReport}.
 * @author Juan Martín Sotuyo Dodero
 */
public class TextTimingReportRenderer implements TimingReportRenderer {

    private static final String TIME_FORMAT = "{0,number,0.0000}";
    private static final String CUSTOM_COUNTER_FORMAT = "{0,number,###,###,###}";

    private static final int LABEL_COLUMN_WIDTH = 50;
    private static final int TIME_COLUMN_WIDTH = 12;
    private static final int SELF_TIME_COLUMN_WIDTH = 17;
    private static final int CALL_COLUMN_WIDTH = 9;
    private static final int COUNTER_COLUMN_WIDTH = 12;

    private static final int COLUMNS = LABEL_COLUMN_WIDTH + TIME_COLUMN_WIDTH
            + SELF_TIME_COLUMN_WIDTH + CALL_COLUMN_WIDTH + COUNTER_COLUMN_WIDTH;

    @Override
    public void render(final TimingReport report, final Writer writer) throws IOException {
        for (final TimedOperationCategory category : TimedOperationCategory.values()) {
            final Map<String, TimedResult> labeledMeasurements = report.getLabeledMeasurements(category);
            if (!labeledMeasurements.isEmpty()) {
                renderCategoryMeasurements(category, labeledMeasurements, writer);
            }
        }

        renderHeader("Summary", writer);

        for (final TimedOperationCategory category : TimedOperationCategory.values()) {
            final TimedResult timedResult = report.getUnlabeledMeasurements(category);
            if (timedResult != null) {
                renderMeasurement(category.displayName(), timedResult, writer);
            }
        }

        writer.write(PMD.EOL);
        renderHeader("Total", writer);

        writer.write(StringUtils.rightPad("Wall Clock Time", LABEL_COLUMN_WIDTH));
        final String wallClockTime = MessageFormat.format(TIME_FORMAT, report.getWallClockMillis() / 1000.0);
        writer.write(StringUtils.leftPad(wallClockTime, TIME_COLUMN_WIDTH));
        writer.write(PMD.EOL);

        writer.flush();
    }

    private void renderMeasurement(final String label, final TimedResult timedResult,
            final Writer writer) throws IOException {
        writer.write(StringUtils.rightPad(label, LABEL_COLUMN_WIDTH));

        final String time = MessageFormat.format(TIME_FORMAT, timedResult.totalTimeNanos.get() / 1000000000.0);
        writer.write(StringUtils.leftPad(time, TIME_COLUMN_WIDTH));

        final String selfTime = MessageFormat.format(TIME_FORMAT, timedResult.selfTimeNanos.get() / 1000000000.0);
        writer.write(StringUtils.leftPad(selfTime, SELF_TIME_COLUMN_WIDTH));

        if (timedResult.callCount.get() > 0) {
            final String callCount = MessageFormat.format(CUSTOM_COUNTER_FORMAT, timedResult.callCount.get());
            writer.write(StringUtils.leftPad(callCount, CALL_COLUMN_WIDTH));

            if (timedResult.extraDataCounter.get() > 0) {
                final String counter = MessageFormat.format(CUSTOM_COUNTER_FORMAT, timedResult.extraDataCounter.get());
                writer.write(StringUtils.leftPad(counter, COUNTER_COLUMN_WIDTH));
            }
        }

        writer.write(PMD.EOL);
    }

    private void renderCategoryMeasurements(final TimedOperationCategory category,
            final Map<String, TimedResult> labeledMeasurements, final Writer writer) throws IOException {
        renderHeader(category.displayName(), writer);

        final TimedResult grandTotal = new TimedResult();
        final TreeSet<Map.Entry<String, TimedResult>> sortedKeySet = new TreeSet<>(
            new Comparator<Map.Entry<String, TimedResult>>() {
                @Override
                public int compare(final Entry<String, TimedResult> o1, final Entry<String, TimedResult> o2) {
                    return Long.compare(o1.getValue().selfTimeNanos.get(), o2.getValue().selfTimeNanos.get());
                }
            });
        sortedKeySet.addAll(labeledMeasurements.entrySet());

        for (final Map.Entry<String, TimedResult> entry : sortedKeySet) {
            renderMeasurement(entry.getKey(), entry.getValue(), writer);
            grandTotal.mergeTimes(entry.getValue());
        }

        writer.write(PMD.EOL);
        renderMeasurement("Total " + category.displayName(), grandTotal, writer);
        writer.write(PMD.EOL);
    }

    private void renderHeader(final String displayName, final Writer writer) throws IOException {
        final StringBuilder sb = new StringBuilder(COLUMNS)
                .append(displayName);

        // Make sure we have an even-length string
        if (displayName.length() % 2 == 1) {
            sb.append(' ');
        }

        // Surround with <<< and >>>
        sb.insert(0, "<<< ").append(" >>>");

        // Create the ruler
        while (sb.length() < COLUMNS) {
            sb.insert(0, '-').append('-');
        }

        writer.write(sb.toString());
        writer.write(PMD.EOL);

        // Write table titles
        writer.write(StringUtils.rightPad("Label", LABEL_COLUMN_WIDTH));
        writer.write(StringUtils.leftPad("Time (secs)", TIME_COLUMN_WIDTH));
        writer.write(StringUtils.leftPad("Self Time (secs)", SELF_TIME_COLUMN_WIDTH));
        writer.write(StringUtils.leftPad("# Calls", CALL_COLUMN_WIDTH));
        writer.write(StringUtils.leftPad("Counter", COUNTER_COLUMN_WIDTH));
        writer.write(PMD.EOL);
        writer.write(PMD.EOL);
    }

}
