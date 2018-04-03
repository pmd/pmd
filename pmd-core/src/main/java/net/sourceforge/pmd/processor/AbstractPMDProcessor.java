/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public abstract class AbstractPMDProcessor {

    private static final Logger LOG = Logger.getLogger(AbstractPMDProcessor.class.getName());
    
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
            throw new RuntimeException(ioe);
        }
    }

    protected String filenameFrom(DataSource dataSource) {
        return dataSource.getNiceFileName(configuration.isReportShortNames(), configuration.getInputPaths());
    }

    /**
     * Create instances for each rule defined in the ruleset(s) in the
     * configuration. Please note, that the returned instances <strong>must
     * not</strong> be used by different threads. Each thread must create its
     * own copy of the rules.
     *
     * @param factory The factory used to create the configured rule sets
     * @param report The base report on which to report any configuration errors
     * @return the rules within a rulesets
     */
    protected RuleSets createRuleSets(RuleSetFactory factory, Report report) {
        final RuleSets rs = RulesetsFactoryUtils.getRuleSets(configuration.getRuleSets(), factory);
        
        final Set<Rule> brokenRules = removeBrokenRules(rs);
        for (final Rule rule : brokenRules) {
            report.addConfigError(new Report.ConfigurationError(rule, rule.dysfunctionReason()));
        }
        
        return rs;
    }
    
    /**
     * Remove and return the misconfigured rules from the rulesets and log them
     * for good measure.
     *
     * @param ruleSets RuleSets to prune of broken rules.
     * @return Set<Rule>
     */
    private Set<Rule> removeBrokenRules(final RuleSets ruleSets) {
        final Set<Rule> brokenRules = new HashSet<>();
        ruleSets.removeDysfunctionalRules(brokenRules);

        for (final Rule rule : brokenRules) {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING,
                        "Removed misconfigured rule: " + rule.getName() + "  cause: " + rule.dysfunctionReason());
            }
        }

        return brokenRules;
    }

    public void processFiles(RuleSetFactory ruleSetFactory, List<DataSource> files, RuleContext ctx,
            List<Renderer> renderers) {
        RuleSets rs = createRuleSets(ruleSetFactory, ctx.getReport());
        configuration.getAnalysisCache().checkValidity(rs, configuration.getClassLoader());
        SourceCodeProcessor processor = new SourceCodeProcessor(configuration);

        for (DataSource dataSource : files) {
            String niceFileName = filenameFrom(dataSource);

            runAnalysis(new PmdRunnable(dataSource, niceFileName, renderers, ctx, rs, processor));
        }

        // render base report first - general errors
        renderReports(renderers, ctx.getReport());
        
        // then add analysis results per file
        collectReports(renderers);
    }

    protected abstract void runAnalysis(PmdRunnable runnable);

    protected abstract void collectReports(List<Renderer> renderers);
}
