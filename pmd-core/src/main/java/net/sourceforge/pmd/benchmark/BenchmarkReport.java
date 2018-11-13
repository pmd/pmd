/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Brian Remedios
 */
@Deprecated
public interface BenchmarkReport {

    /**
     *
     * @param stressResults the durations from the stress test run
     * @param stream the report is written into this stream
     */
    void generate(Set<RuleDuration> stressResults, PrintStream stream);

    /**
     *
     * @param benchmarksByName
     * @param stream the report is written into this stream
     */
    void generate(Map<String, BenchmarkResult> benchmarksByName, PrintStream stream);
}
