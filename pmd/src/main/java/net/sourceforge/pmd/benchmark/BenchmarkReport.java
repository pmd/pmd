package net.sourceforge.pmd.benchmark;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.benchmark.Benchmarker.Result;

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
	void generate(Set<Result> stressResults, PrintStream out);
	
	/**
	 * 
	 * @param benchmarksByName
	 * @param out
	 */
	void generate(Map<String, BenchmarkResult> benchmarksByName, PrintStream out);
}
