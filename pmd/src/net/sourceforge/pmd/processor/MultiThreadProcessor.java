/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sourceforge.pmd.Configuration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public class MultiThreadProcessor extends AbstractPMDProcessor {


	public MultiThreadProcessor(final Configuration configuration) {
		super(configuration);
	}
	
	/**
	 * Run PMD on a list of files using multiple threads.
	 */
	public void processFiles(final RuleSetFactory ruleSetFactory, final List<DataSource> files,
			final RuleContext ctx, final List<Renderer> renderers) {

		RuleSets rs = null;
		try {
			rs = ruleSetFactory.createRuleSets(configuration.getRuleSets());
		} catch (RuleSetNotFoundException rsnfe) {
			// should not happen: parent already created a ruleset
		}
		rs.start(ctx);

		PmdThreadFactory factory = new PmdThreadFactory(ruleSetFactory, ctx);
		ExecutorService executor = Executors.newFixedThreadPool(
				configuration.getThreads(), factory);
		List<Future<Report>> tasks = new LinkedList<Future<Report>>();

		for (DataSource dataSource : files) {
			String niceFileName = dataSource.getNiceFileName(
					 configuration.isReportShortNames(), configuration.getInputPaths());

			PmdRunnable r = new PmdRunnable(executor, configuration,
					dataSource, niceFileName, renderers);
			Future<Report> future = executor.submit(r);
			tasks.add(future);
		}
		executor.shutdown();

		while (!tasks.isEmpty()) {
			Future<Report> future = tasks.remove(0);
			Report report = null;
			try {
				report = future.get();
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				future.cancel(true);
			} catch (ExecutionException ee) {
				Throwable t = ee.getCause();
				if (t instanceof RuntimeException) {
					throw (RuntimeException) t;
				} else if (t instanceof Error) {
					throw (Error) t;
				} else {
					throw new IllegalStateException(
							"PmdRunnable exception", t);
				}
			}

			super.renderReports(renderers, report);
		}
		rs.end(ctx);
		super.renderReports(renderers,ctx.getReport());

	}

}
