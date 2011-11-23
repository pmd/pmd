package net.sourceforge.pmd.benchmark;

import java.io.PrintStream;
import java.util.Map;

/**
 * 
 * @author Brian Remedios
 */
public interface BenchmarkReport {

	/**
	 * 
	 * @param benchmarksByName
	 * @param out
	 */
	void generate(Map<String, BenchmarkResult> benchmarksByName, PrintStream out);
}
