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
public interface BenchmarkReport {

	/**
	 * 
	 * @param stressResults
	 * @param out
	 */
	void generate(Set<RuleDuration> stressResults, PrintStream out);
	
	/**
	 * 
	 * @param benchmarksByName
	 * @param out
	 */
	void generate(Map<String, BenchmarkResult> benchmarksByName, PrintStream out);
}
