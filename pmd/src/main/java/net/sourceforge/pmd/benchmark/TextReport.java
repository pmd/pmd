package net.sourceforge.pmd.benchmark;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.StringUtil;

public class TextReport implements BenchmarkReport {

	private static final int NAME_COLUMN_WIDTH	= 50;

	public TextReport() {

	}

	public void report(Map<String, BenchmarkResult> benchmarksByName) {
		generate(benchmarksByName, System.out);
	}
	
	public void generate(Map<String, BenchmarkResult> benchmarksByName, PrintStream out) {

		List<BenchmarkResult> results = new ArrayList<BenchmarkResult>(benchmarksByName.values());

		long totalTime[] = new long[Benchmark.TotalPMD.index + 1];
		long totalCount[] = new long[Benchmark.TotalPMD.index + 1];

		for (BenchmarkResult benchmarkResult: results) {
			totalTime[benchmarkResult.getType().index] += benchmarkResult.getTime();
			totalCount[benchmarkResult.getType().index] += benchmarkResult.getCount();
			if (benchmarkResult.getType().index < Benchmark.MeasuredTotal.index) {
				totalTime[Benchmark.MeasuredTotal.index] += benchmarkResult.getTime();
			}
		}
		results.add(new BenchmarkResult(Benchmark.RuleTotal, 		totalTime[Benchmark.RuleTotal.index], 0));
		results.add(new BenchmarkResult(Benchmark.RuleChainTotal, 	totalTime[Benchmark.RuleChainTotal.index], 0));
		results.add(new BenchmarkResult(Benchmark.MeasuredTotal, 	totalTime[Benchmark.MeasuredTotal.index], 0));
		results.add(new BenchmarkResult(Benchmark.NonMeasuredTotal, totalTime[Benchmark.TotalPMD.index] - totalTime[Benchmark.MeasuredTotal.index], 0));
		Collections.sort(results);

		StringBuilder buf = new StringBuilder();
		boolean writeRuleHeader = true;
		boolean writeRuleChainRuleHeader = true;
		long ruleCount = 0;
		long ruleChainCount = 0;

		for (BenchmarkResult benchmarkResult: results) {
			StringBuilder buf2 = new StringBuilder(benchmarkResult.getName());
			buf2.append(':');
			while (buf2.length() <= NAME_COLUMN_WIDTH) {
				buf2.append(' ');
			}
			buf2.append(StringUtil.lpad(MessageFormat.format("{0,number,0.000}", Double.valueOf(benchmarkResult.getTime()/1000000000.0)), 8));
			if (benchmarkResult.getType().index <= Benchmark.RuleChainRule.index) {
				buf2.append(StringUtil.lpad(MessageFormat.format("{0,number,###,###,###,###,###}", benchmarkResult.getCount()), 20));
			}
			switch (benchmarkResult.getType()) {
			case Rule:
				if (writeRuleHeader) {
					writeRuleHeader = false;
					buf.append(PMD.EOL);
					buf.append("---------------------------------<<< Rules >>>---------------------------------" + PMD.EOL);
					buf.append("Rule name                                       Time (secs)    # of Evaluations" + PMD.EOL);
					buf.append(PMD.EOL);
				}
				ruleCount++;
				break;
			case RuleChainRule:
				if (writeRuleChainRuleHeader) {
					writeRuleChainRuleHeader = false;
					buf.append(PMD.EOL);
					buf.append("----------------------------<<< RuleChain Rules >>>----------------------------" + PMD.EOL);
					buf.append("Rule name                                       Time (secs)         # of Visits" + PMD.EOL);
					buf.append(PMD.EOL);
				}
				ruleChainCount++;
				break;
			case CollectFiles:
				buf.append(PMD.EOL);
				buf.append("--------------------------------<<< Summary >>>--------------------------------" + PMD.EOL);
				buf.append("Segment                                         Time (secs)" + PMD.EOL);
				buf.append(PMD.EOL);
				break;
			case MeasuredTotal:
				String s = MessageFormat.format("{0,number,###,###,###,###,###}", ruleCount);
				buf.append("Rule Average (" + s + " rules):" + StringUtil.lpad(MessageFormat.format("{0,number,0.000}", ruleCount==0?0:totalTime[Benchmark.Rule.index]/1000000000.0d/ruleCount), 37-s.length()) + PMD.EOL);
				s = MessageFormat.format("{0,number,###,###,###,###,###}", ruleChainCount);
				buf.append("RuleChain Average (" + s + " rules):" + StringUtil.lpad(MessageFormat.format("{0,number,0.000}", ruleChainCount==0?0:totalTime[Benchmark.RuleChainRule.index]/1000000000.0d/ruleChainCount), 32-s.length()) + PMD.EOL);

				buf.append(PMD.EOL);
				buf.append("-----------------------------<<< Final Summary >>>-----------------------------" + PMD.EOL);
				buf.append("Total                                           Time (secs)" + PMD.EOL);
				buf.append(PMD.EOL);
				break;
			default:
				// Do nothing
				break;
			}
			buf.append(buf2.toString());
			buf.append(PMD.EOL);
		}
		
		out.print(buf.toString());
	}
}
