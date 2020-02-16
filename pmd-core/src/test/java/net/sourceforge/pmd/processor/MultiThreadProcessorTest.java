/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.ThreadSafeReportListener;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.renderers.AbstractAccumulatingRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.internal.AbstractDataSource;

public class MultiThreadProcessorTest {

    private RuleContext ctx;
    private MultiThreadProcessor processor;
    private RuleSetFactory ruleSetFactory;
    private List<DataSource> files;
    private SimpleReportListener reportListener;

    public void setUpForTest(final String ruleset) {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setRuleSets(ruleset);
        configuration.setThreads(2);
        files = new ArrayList<>();
        files.add(new StringDataSource("file1-violation.dummy", "ABC"));
        files.add(new StringDataSource("file2-foo.dummy", "DEF"));

        reportListener = new SimpleReportListener();
        ctx = new RuleContext();
        ctx.getReport().addListener(reportListener);

        processor = new MultiThreadProcessor(configuration);
        ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
    }

    @Test
    public void testRulesDysnfunctionalLog() throws IOException {
        setUpForTest("rulesets/MultiThreadProcessorTest/dysfunctional.xml");
        final SimpleRenderer renderer = new SimpleRenderer(null, null);
        renderer.start();
        processor.processFiles(ruleSetFactory, files, ctx, Collections.<Renderer>singletonList(renderer));
        renderer.end();

        final Iterator<ConfigurationError> configErrors = renderer.getReport().configErrors();
        final ConfigurationError error = configErrors.next();

        Assert.assertEquals("Dysfunctional rule message not present",
                DysfunctionalRule.DYSFUNCTIONAL_RULE_REASON, error.issue());
        Assert.assertEquals("Dysfunctional rule is wrong",
                DysfunctionalRule.class, error.rule().getClass());
        Assert.assertFalse("More configuration errors found than expected", configErrors.hasNext());
    }

    @Test
    public void testRulesThreadSafety() {
        setUpForTest("rulesets/MultiThreadProcessorTest/basic.xml");
        processor.processFiles(ruleSetFactory, files, ctx, Collections.<Renderer>emptyList());

        // if the rule is not executed, then maybe a
        // ConcurrentModificationException happened
        Assert.assertEquals("Test rule has not been executed", 2, NotThreadSafeRule.count.get());
        // if the violation is not reported, then the rule instances have been
        // shared between the threads
        Assert.assertEquals("Missing violation", 1, reportListener.violations.get());
    }

    private static class StringDataSource extends AbstractDataSource {
        private final String data;
        private final String name;

        StringDataSource(String name, String data) {
            this.name = name;
            this.data = data;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data.getBytes("UTF-8"));
        }

        @Override
        public String getNiceFileName(boolean shortNames, String inputFileName) {
            return name;
        }
    }

    public static class NotThreadSafeRule extends AbstractRule {
        public static AtomicInteger count = new AtomicInteger(0);
        private boolean hasViolation; // this variable will be overridden
        // between the threads

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
            count.incrementAndGet();

            if (ctx.getSourceCodeFilename().contains("violation")) {
                hasViolation = true;
            } else {
                letTheOtherThreadRun(10);
                hasViolation = false;
            }

            letTheOtherThreadRun(100);
            if (hasViolation) {
                addViolation(ctx, nodes.get(0));
            }
        }

        private void letTheOtherThreadRun(int millis) {
            try {
                Thread.yield();
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }

    public static class DysfunctionalRule extends AbstractRule {

        public static final String DYSFUNCTIONAL_RULE_REASON = "dysfunctional rule is dysfunctional";

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
            // noop
        }

        @Override
        public String dysfunctionReason() {
            return DYSFUNCTIONAL_RULE_REASON;
        }
    }

    private static class SimpleReportListener implements ThreadSafeReportListener {
        public AtomicInteger violations = new AtomicInteger(0);

        @Override
        public void ruleViolationAdded(RuleViolation ruleViolation) {
            violations.incrementAndGet();
        }

        @Override
        public void metricAdded(Metric metric) {
        }
    }

    private static class SimpleRenderer extends AbstractAccumulatingRenderer {

        /* default */ SimpleRenderer(String name, String description) {
            super(name, description);
        }

        @Override
        public String defaultFileExtension() {
            return null;
        }

        @Override
        public void end() throws IOException {
        }

        public Report getReport() {
            return report;
        }
    }
}
