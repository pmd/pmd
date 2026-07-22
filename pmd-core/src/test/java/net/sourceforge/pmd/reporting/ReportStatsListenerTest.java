/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class ReportStatsListenerTest {

    /**
     * This test fires events from multiple threads and checks that the total number matches in the end.
     */
    @Test
    void testConcurrency() throws InterruptedException {
        int numFiles = 1000;

        int totalErrors = 0;
        int totalViolations = 0;

        Random r = new Random();

        ReportStatsListener subject = new ReportStatsListener();

        ExecutorService executor = Executors.newFixedThreadPool(numFiles);

        for (int i = 0; i < numFiles; i++) {
            int numErrors = r.nextInt(10);
            int numViolations = r.nextInt(100);

            totalErrors += numErrors;
            totalViolations += numViolations;

            executor.submit(() -> {
                try (FileAnalysisListener fileAnalysisListener = subject.startFileAnalysis(null)) {
                    List<Runnable> events = new ArrayList<>();
                    for (int j = 0; j < numErrors; j++) {
                        events.add(() -> fileAnalysisListener.onError(null));
                    }
                    for (int j = 0; j < numViolations; j++) {
                        events.add(() -> fileAnalysisListener.onRuleViolation(null));
                    }
                    Collections.shuffle(events);

                    for (Runnable event : events) {
                        event.run();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // If we ever update to Java >= 19, replace the following two lines with executor.close() (or try-with-resources)
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        subject.close();

        ReportStats stats = subject.getResult();

        assertEquals(totalErrors, stats.getNumErrors());
        assertEquals(totalViolations, stats.getNumViolations());
    }
}
