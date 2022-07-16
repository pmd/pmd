/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.cache.AnalysisCache;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener.ViolationCounterListener;

class GlobalListenerTest {

    static final int NUM_DATA_SOURCES = 3;

    @Test
    void testViolationCounter() throws Exception {

        PMDConfiguration config = newConfig();

        ViolationCounterListener listener = new GlobalAnalysisListener.ViolationCounterListener();

        MyFooRule mockrule = Mockito.spy(MyFooRule.class);
        runPmd(config, listener, mockrule);

        Mockito.verify(mockrule, times(NUM_DATA_SOURCES)).apply(any(), any());
        assertEquals(2, (int) listener.getResult());

    }

    @Test
    void testViolationCounterOnMulti() throws Exception {

        PMDConfiguration config = newConfig();
        config.setThreads(2);

        ViolationCounterListener listener = new GlobalAnalysisListener.ViolationCounterListener();

        MyFooRule mockrule = Mockito.spy(MyFooRule.class);
        when(mockrule.deepCopy()).thenReturn(mockrule); // the spy cannot track the deep copies

        runPmd(config, listener, mockrule);

        Mockito.verify(mockrule, times(NUM_DATA_SOURCES)).apply(any(), any());
        assertEquals(2, (int) listener.getResult());

    }

    @Test
    void testAnalysisCache() throws Exception {

        PMDConfiguration config = newConfig();
        AnalysisCache mockCache = spy(NoopAnalysisCache.class);
        config.setAnalysisCache(mockCache);

        MyFooRule rule = new MyFooRule();
        runPmd(config, GlobalAnalysisListener.noop(), rule);

        verify(mockCache).checkValidity(any(), any());
        verify(mockCache, times(1)).persist();
        verify(mockCache, times(NUM_DATA_SOURCES)).isUpToDate(any());
    }

    @Test
    void testCacheWithFailure() throws Exception {

        PMDConfiguration config = newConfig();
        AnalysisCache mockCache = spy(NoopAnalysisCache.class);
        config.setAnalysisCache(mockCache);

        BrokenRule rule = new BrokenRule();  // the broken rule throws
        runPmd(config, GlobalAnalysisListener.noop(), rule);

        // cache methods are called regardless
        verify(mockCache).checkValidity(any(), any());
        verify(mockCache, times(1)).persist();
        verify(mockCache, times(NUM_DATA_SOURCES)).isUpToDate(any());
    }

    @Test
    void testCacheWithPropagatedException() throws Exception {

        PMDConfiguration config = newConfig();
        AnalysisCache mockCache = spy(NoopAnalysisCache.class);
        config.setAnalysisCache(mockCache);

        BrokenRule rule = new BrokenRule();  // the broken rule throws
        // now the exception should be propagated
        GlobalAnalysisListener listener = GlobalAnalysisListener.exceptionThrower();
        FileAnalysisException exception = assertThrows(FileAnalysisException.class, () -> {
            runPmd(config, listener, rule);
        });

        assertEquals("fname1.dummy", exception.getFileName());

        // cache methods are called regardless
        verify(mockCache).checkValidity(any(), any());
        verify(mockCache, times(1)).persist();
        verify(mockCache, times(1)).isUpToDate(any());
    }

    @NonNull
    private PMDConfiguration newConfig() {
        PMDConfiguration config = new PMDConfiguration();
        config.setAnalysisCache(new NoopAnalysisCache());
        config.setIgnoreIncrementalAnalysis(true);
        config.setThreads(1);
        return config;
    }

    private void runPmd(PMDConfiguration config, GlobalAnalysisListener listener, Rule rule) {
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.files().addSourceFile("fname1.dummy", "abc");
            pmd.files().addSourceFile("fname2.dummy", "abcd");
            pmd.files().addSourceFile("fname21.dummy", "abcd");
            pmd.addListener(listener);
            pmd.performAnalysis();
        }
    }


    public static class MyFooRule extends FooRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            if (node.getTextDocument().getDisplayName().contains("1")) {
                ctx.addViolation(node);
            }
        }
    }

    public static class BrokenRule extends FooRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            throw new IllegalArgumentException("Something happened");
        }
    }
}
