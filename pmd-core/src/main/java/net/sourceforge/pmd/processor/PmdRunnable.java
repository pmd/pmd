/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

public class PmdRunnable extends PMD implements Callable<Report> {

	private static final Logger LOG = Logger.getLogger(PmdRunnable.class.getName());

	private final ExecutorService executor;
	private final DataSource dataSource;
	private final String fileName;
	private final List<Renderer> renderers;

	public PmdRunnable(ExecutorService executor,
			PMDConfiguration configuration, DataSource dataSource,
			String fileName, List<Renderer> renderers) {
		super(configuration);
		this.executor = executor;
		this.dataSource = dataSource;
		this.fileName = fileName;
		this.renderers = renderers;
	}

	// If we ever end up having a ReportUtil class, this method should be moved there...
	private static void addError(Report report, Exception ex, String fileName) {
		report.addError(
				new Report.ProcessingError(ex.getMessage(),
				fileName)
				);
	}

	private void addErrorAndShutdown(Report report, Exception e, String errorMessage) {
		// unexpected exception: log and stop executor service
		LOG.log(Level.FINE, errorMessage, e);
		addError(report, e, fileName);
		executor.shutdownNow();
	}
	
	@Override
    public Report call() {
		PmdThread thread = (PmdThread) Thread.currentThread();

		RuleContext ctx = thread.getRuleContext();
		RuleSets rs = thread.getRuleSets(configuration.getRuleSets());

		Report report = setupReport(rs, ctx, fileName);
		
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Processing " + ctx.getSourceCodeFilename());
		}
		for (Renderer r : renderers) {
			r.startFileAnalysis(dataSource);
		}

		try {
			InputStream stream = new BufferedInputStream(
					dataSource.getInputStream());
			ctx.setLanguageVersion(null);
			this.getSourceCodeProcessor().processSourceCode(stream, rs, ctx);
		} catch (PMDException pmde) {
		    if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Error while processing file: "+fileName, pmde.getCause());
		    }
			addError(report, pmde, fileName);
		} catch (IOException ioe) {
			addErrorAndShutdown(report, ioe, "IOException during processing of "+ fileName );

		} catch (RuntimeException re) {
			addErrorAndShutdown(report, re,"RuntimeException during processing of " + fileName);
		}
		return report;
	}
	
	private static class PmdThread extends Thread {

        private final int id;
        private RuleContext context;
        private RuleSets rulesets;
        private final RuleSetFactory ruleSetFactory;

		public PmdThread(int id, Runnable r, RuleSetFactory ruleSetFactory,
				RuleContext ctx) {
			super(r, "PmdThread " + id);
			this.id = id;
			context = new RuleContext(ctx);
			this.ruleSetFactory = ruleSetFactory;
		}

		public RuleContext getRuleContext() {
			return context;
		}

		public RuleSets getRuleSets(String rsList) {
			if (rulesets == null) {
				try {
					rulesets = ruleSetFactory.createRuleSets(rsList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return rulesets;
		}

		@Override
		public String toString() {
			return "PmdThread " + id;
		}
	}

	public static Thread createThread(int id, Runnable r,
			RuleSetFactory ruleSetFactory, RuleContext ctx) {
		return new PmdThread(id, r,ruleSetFactory, ctx);
	}
}
