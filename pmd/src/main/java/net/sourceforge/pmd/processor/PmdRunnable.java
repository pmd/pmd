package net.sourceforge.pmd.processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Configuration;
import net.sourceforge.pmd.PMD;
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
			Configuration configuration, DataSource dataSource,
			String fileName, List<Renderer> renderers) {
		super(configuration);
		this.executor = executor;
		this.dataSource = dataSource;
		this.fileName = fileName;
		this.renderers = renderers;
	}

	private void addError(Report report, Exception ex) {
		report.addError(
				new Report.ProcessingError(ex.getMessage(),
				fileName)
				);
	}
	
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
			LOG.log(Level.FINE, "Error while processing file", pmde.getCause());

			addError(report, pmde);
		} catch (IOException ioe) {
			// unexpected exception: log and stop executor service
			LOG.log(Level.FINE, "IOException during processing", ioe);

			addError(report, ioe);

			executor.shutdownNow();
		} catch (RuntimeException re) {
			// unexpected exception: log and stop executor service
			LOG.log(Level.FINE, "RuntimeException during processing", re);

			addError(report, re);

			executor.shutdownNow();
		}
		return report;
	}

	private static class PmdThread extends Thread {

		public PmdThread(int id, Runnable r, RuleSetFactory ruleSetFactory,
				RuleContext ctx) {
			super(r, "PmdThread " + id);
			this.id = id;
			context = new RuleContext(ctx);
			this.ruleSetFactory = ruleSetFactory;
		}

		private final int id;
		private RuleContext context;
		private RuleSets rulesets;
		private final RuleSetFactory ruleSetFactory;

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
