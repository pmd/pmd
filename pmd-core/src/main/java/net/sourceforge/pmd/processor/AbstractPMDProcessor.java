/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.processor;

import java.io.IOException;
import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public abstract class AbstractPMDProcessor {

	protected final PMDConfiguration configuration;

	public AbstractPMDProcessor(PMDConfiguration configuration) {
		this.configuration = configuration;
	}

	public void renderReports(final List<Renderer> renderers, final Report report) {
		
		long start = System.nanoTime();

		try {	
			for (Renderer r : renderers) {
				r.renderFileReport(report);
			}
			long end = System.nanoTime();
			Benchmarker.mark(Benchmark.Reporting, end - start, 1);
		} catch (IOException ioe) {

		}
	}

	protected String filenameFrom(DataSource dataSource) {
		return dataSource.getNiceFileName(
			 configuration.isReportShortNames(), 
			 configuration.getInputPaths()
			 );
	}

	/**
	 * Create instances for each rule defined in the ruleset(s) in
	 * the configuration.
	 * Please note, that the returned instances <strong>must not</strong>
	 * be used by different threads. Each thread must create its own
	 * copy of the rules (see {@link PmdRunnable.PmdThread#getRuleSets(String)}).
	 * @param factory
	 * @return the rules within a rulesets
	 */
	protected RuleSets createRuleSets(RuleSetFactory factory) {
		return RulesetsFactoryUtils.getRuleSets(configuration.getRuleSets(), factory);
	}
	
}
