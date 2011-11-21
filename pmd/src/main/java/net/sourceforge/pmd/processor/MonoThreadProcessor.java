/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Configuration;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
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
public final class MonoThreadProcessor extends AbstractPMDProcessor {

	public MonoThreadProcessor(Configuration configuration) {
		super(configuration);
	}

	private static final Logger LOG = Logger.getLogger(MonoThreadProcessor.class.getName());

	public void processFiles(RuleSetFactory ruleSetFactory, List<DataSource> files,
			RuleContext ctx, List<Renderer> renderers) {

		// single threaded execution
		PMD pmd = new PMD(configuration);

		RuleSets rs = null;
		try {
			rs = ruleSetFactory.createRuleSets(configuration.getRuleSets());
		} catch (RuleSetNotFoundException rsnfe) {
			// should not happen: parent already created a ruleset
		}

		for (DataSource dataSource : files) {
			String niceFileName = dataSource.getNiceFileName(
					configuration.isReportShortNames(), configuration.getInputPaths());
					
			Report report = PMD.setupReport(rs, ctx, niceFileName);
			
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Processing " + ctx.getSourceCodeFilename());
			}
			rs.start(ctx);

			for (Renderer r : renderers) {
				r.startFileAnalysis(dataSource);
			}

			try {
				InputStream stream = new BufferedInputStream(
						dataSource.getInputStream());
				ctx.setLanguageVersion(null);
				pmd.getSourceCodeProcessor().processSourceCode(stream, rs, ctx);
			} catch (PMDException pmde) {
				LOG.log(Level.FINE, "Error while processing file",
						pmde.getCause());

				report.addError(new Report.ProcessingError(pmde
						.getMessage(), niceFileName));
			} catch (IOException ioe) {
				// unexpected exception: log and stop executor service
				LOG.log(Level.FINE, "Unable to read source file", ioe);

				report.addError(new Report.ProcessingError(
						ioe.getMessage(), niceFileName));
			} catch (RuntimeException re) {
				// unexpected exception: log and stop executor service
				LOG.log(Level.FINE,
						"RuntimeException while processing file", re);

				report.addError(new Report.ProcessingError(re.getMessage(),
						niceFileName));
			}

			rs.end(ctx);
			super.renderReports(renderers, ctx.getReport());
		}
	}
}
