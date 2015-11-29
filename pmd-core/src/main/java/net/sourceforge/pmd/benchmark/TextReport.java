/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.benchmark;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.StringUtil;

/**
 *
 *
 */
public class TextReport implements BenchmarkReport {


	private static final int TIME_COLUMN		 = 48;
	private static final int NAME_COLUMN_WIDTH	 = 50;
	private static final int VALUE_COLUMN_WIDTH	 =  8;

	/**
	 *
	 * @param stressResults Set<Result>
	 * @param out PrintStream
	 * @see net.sourceforge.pmd.benchmark.BenchmarkReport#generate(Set<Result>, PrintStream)
	 */
	public void generate(Set<RuleDuration> stressResults, PrintStream out) {

		  out.println("=========================================================");
          out.println("Rule\t\t\t\t\t\tTime in ms");
          out.println("=========================================================");

          for (RuleDuration result: stressResults) {
              StringBuilder buffer = new StringBuilder(result.rule.getName());
              while (buffer.length() < TIME_COLUMN) {
            	  buffer.append(' ');
              }
              buffer.append(result.time);
              out.println(out.toString());
          }

          out.println("=========================================================");
	}

	/**
	 *
	 * @param benchmarksByName Map<String,BenchmarkResult>
	 */
	public void report(Map<String, BenchmarkResult> benchmarksByName) {
		generate(benchmarksByName, System.out);
	}

	/**
	 *
	 * @param benchmarksByName Map<String,BenchmarkResult>
	 * @param out PrintStream
	 * @see net.sourceforge.pmd.benchmark.BenchmarkReport#generate(Map<String,BenchmarkResult>, PrintStream)
	 */
	public void generate(Map<String, BenchmarkResult> benchmarksByName, PrintStream out) {

		List<BenchmarkResult> results = new ArrayList<>(benchmarksByName.values());

		long[] totalTime = new long[Benchmark.TotalPMD.index + 1];
		long[] totalCount = new long[Benchmark.TotalPMD.index + 1];

		for (BenchmarkResult benchmarkResult: results) {
			totalTime[benchmarkResult.type.index] += benchmarkResult.getTime();
			totalCount[benchmarkResult.type.index] += benchmarkResult.getCount();
			if (benchmarkResult.type.index < Benchmark.MeasuredTotal.index) {
				totalTime[Benchmark.MeasuredTotal.index] += benchmarkResult.getTime();
			}
		}
		results.add(new BenchmarkResult(Benchmark.RuleTotal, 		totalTime[Benchmark.RuleTotal.index], 0));
		results.add(new BenchmarkResult(Benchmark.RuleChainTotal, 	totalTime[Benchmark.RuleChainTotal.index], 0));
		results.add(new BenchmarkResult(Benchmark.MeasuredTotal, 	totalTime[Benchmark.MeasuredTotal.index], 0));
		results.add(new BenchmarkResult(Benchmark.NonMeasuredTotal, totalTime[Benchmark.TotalPMD.index] - totalTime[Benchmark.MeasuredTotal.index], 0));
		Collections.sort(results);

		StringBuilderCR buf = new StringBuilderCR(PMD.EOL);
		boolean writeRuleHeader = true;
		boolean writeRuleChainRuleHeader = true;
		long ruleCount = 0;
		long ruleChainCount = 0;

		for (BenchmarkResult benchmarkResult: results) {
			StringBuilder buf2 = new StringBuilder(benchmarkResult.name);
			buf2.append(':');
			while (buf2.length() <= NAME_COLUMN_WIDTH) {
				buf2.append(' ');
			}
			String result = MessageFormat.format("{0,number,0.000}", Double.valueOf(benchmarkResult.getTime()/1000000000.0));
			buf2.append(StringUtil.lpad(result, VALUE_COLUMN_WIDTH));
			if (benchmarkResult.type.index <= Benchmark.RuleChainRule.index) {
				buf2.append(StringUtil.lpad(MessageFormat.format("{0,number,###,###,###,###,###}", benchmarkResult.getCount()), 20));
			}
			switch (benchmarkResult.type) {
			case Rule:
				if (writeRuleHeader) {
					writeRuleHeader = false;
					buf.appendLn();
					buf.appendLn("---------------------------------<<< Rules >>>---------------------------------");
					buf.appendLn("Rule name                                       Time (secs)    # of Evaluations");
					buf.appendLn();
				}
				ruleCount++;
				break;
			case RuleChainRule:
				if (writeRuleChainRuleHeader) {
					writeRuleChainRuleHeader = false;
					buf.appendLn();
					buf.appendLn("----------------------------<<< RuleChain Rules >>>----------------------------");
					buf.appendLn("Rule name                                       Time (secs)         # of Visits");
					buf.appendLn();
				}
				ruleChainCount++;
				break;
			case CollectFiles:
				buf.appendLn();
				buf.appendLn("--------------------------------<<< Summary >>>--------------------------------");
				buf.appendLn("Segment                                         Time (secs)");
				buf.appendLn();
				break;
			case MeasuredTotal:
				String s = MessageFormat.format("{0,number,###,###,###,###,###}", ruleCount);
				String t = MessageFormat.format("{0,number,0.000}", ruleCount==0 ? 0 : total(totalTime,Benchmark.Rule,ruleCount));
				buf.appendLn("Rule Average (", s, " rules):", StringUtil.lpad(t, 37-s.length()));
				s = MessageFormat.format("{0,number,###,###,###,###,###}", ruleChainCount);
				t = MessageFormat.format("{0,number,0.000}", ruleChainCount==0 ? 0 : total(totalTime,Benchmark.RuleChainRule, ruleChainCount));
				buf.appendLn("RuleChain Average (", s, " rules):", StringUtil.lpad(t, 32-s.length()));

				buf.appendLn();
				buf.appendLn("-----------------------------<<< Final Summary >>>-----------------------------");
				buf.appendLn("Total                                           Time (secs)");
				buf.appendLn();
				break;
			default:
				// Do nothing
				break;
			}
			buf.appendLn(buf2.toString());
		}

		out.print(buf.toString());
	}

	/**
	 *
	 * @param timeTotals long[]
	 * @param index Benchmark
	 * @param count long
	 * @return double
	 */
	private static double total(long[] timeTotals, Benchmark index, long count) {
		return timeTotals[index.index]/1000000000.0d/count;
	}
}
