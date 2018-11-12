/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.benchmark.TimeTracker.TimedResult;
import net.sourceforge.pmd.lang.Language;


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

        // TODO we could render language-specific categories separately
        Map<TimedOperationCategory, TimedResult> unlabeled = new HashMap<>();

        for (TimedOperationCategory cat : report.getAllMeasurements().keySet()) {
            Map<String, TimedResult> measurements = report.getAllMeasurements().get(cat);

            TimedResult result = measurements.remove(null);
            if (result != null) {
                unlabeled.put(cat, result);
            }
            if (!measurements.isEmpty()) {
                renderCategoryMeasurements(cat, measurements, writer);
            }

        }

        renderHeader("Summary", writer);

        List<TimedOperationCategory> rest = new ArrayList<>(unlabeled.keySet());
        Collections.sort(rest);
        Map<Optional<Language>, List<TimedOperationCategory>> collect
                = unlabeled.keySet().stream().collect(groupingBy(TimedOperationCategory::getLanguage));

        Map<Optional<Language>, Map<TimedOperationCategory, TimedResult>> langToResults
                = mapValues(collect, lst -> subMap(unlabeled, lst));

        for (Optional<Language> lang : langToResults.keySet()) {

            if (lang.isPresent()) {
                writer.write(lang.get().getName());
                writer.write(":");
                writer.write(PMD.EOL);
            }

            Map<TimedOperationCategory, TimedResult> categoryToResult = langToResults.get(lang);

            List<TimedOperationCategory> cats = new ArrayList<>(langToResults.get(lang).keySet());
            Collections.sort(cats);

            for (TimedOperationCategory category : cats) {
                renderMeasurement(lang.isPresent(), category.displayName(), unlabeled.get(category), writer);
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


    private void renderMeasurement(boolean indent, final String label, final TimedResult timedResult, final Writer writer) throws IOException {
        final String identString = indent ? "  " : "";
        writer.write(identString);
        writer.write(StringUtils.rightPad(label, LABEL_COLUMN_WIDTH - identString.length()));
        
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


    private <K, I, O> Map<K, O> mapValues(Map<K, I> map, Function<I, O> f) {
        return map.keySet().stream().collect(toMap(k -> k, k -> f.apply(map.get(k))));
    }


    private <K, I> Map<K, I> subMap(Map<K, I> map, List<K> keysToKeep) {
        return keysToKeep.stream().collect(toMap(k -> k, map::get));
    }


    private void renderCategoryMeasurements(final TimedOperationCategory category,
            final Map<String, TimedResult> labeledMeasurements, final Writer writer) throws IOException {
        renderHeader(category.displayName(), writer);

        final TimedResult grandTotal = new TimedResult();
        final SortedSet<Entry<String, TimedResult>> sortedKeySet = new TreeSet<>(Comparator.comparingLong(o -> o.getValue().selfTimeNanos.get()));
        sortedKeySet.addAll(labeledMeasurements.entrySet());

        for (final Map.Entry<String, TimedResult> entry : sortedKeySet) {
            renderMeasurement(false, entry.getKey(), entry.getValue(), writer);
            grandTotal.mergeTimes(entry.getValue());
        }

        writer.write(PMD.EOL);
        renderMeasurement(false, "Total " + category.displayName(), grandTotal, writer);
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
