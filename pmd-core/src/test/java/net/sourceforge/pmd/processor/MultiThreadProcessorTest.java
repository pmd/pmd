/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.datasource.DataSource;

public class MultiThreadProcessorTest {

    @Test
    public void testRulesThreadSafety() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setRuleSets("rulesets/MultiThreadProcessorTest/basic.xml");
        configuration.setThreads(2);
        List<DataSource> files = new ArrayList<>();
        files.add(new StringDataSource("file1-violation.dummy", "ABC"));
        files.add(new StringDataSource("file2-foo.dummy", "DEF"));

        SimpleReportListener reportListener = new SimpleReportListener();
        RuleContext ctx = new RuleContext();
        ctx.getReport().addListener(reportListener);

        MultiThreadProcessor processor = new MultiThreadProcessor(configuration);
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        processor.processFiles(ruleSetFactory, files, ctx, Collections.<Renderer>emptyList());

        // if the rule is not executed, then maybe a
        // ConcurrentModificationException happened
        Assert.assertEquals("Test rule has not been executed", 2, NotThreadSafeRule.count.get());
        // if the violation is not reported, then the rule instances have been
        // shared between the threads
        Assert.assertEquals("Missing violation", 1, reportListener.violations.get());
    }

    private static class StringDataSource implements DataSource {
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

    private static class SimpleReportListener implements ReportListener {
        public AtomicInteger violations = new AtomicInteger(0);

        @Override
        public void ruleViolationAdded(RuleViolation ruleViolation) {
            violations.incrementAndGet();
        }

        @Override
        public void metricAdded(Metric metric) {
        }
    }
}
