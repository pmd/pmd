/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.InternalApiBridgeForTestsOnly;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.cache.internal.AnalysisCache;
import net.sourceforge.pmd.cache.internal.NoopAnalysisCache;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;

class GlobalAnalysisListenerTest {

    static final int NUM_DATA_SOURCES = 3;

    @Test
    void testViolationCounter() {

        PMDConfiguration config = newConfig();

        GlobalAnalysisListener.ViolationCounterListener listener = new GlobalAnalysisListener.ViolationCounterListener();

        MyFooRule mockrule = Mockito.spy(MyFooRule.class);
        runPmd(config, listener, mockrule);

        Mockito.verify(mockrule, times(NUM_DATA_SOURCES)).apply(any(), any());
        assertEquals(2, (int) listener.getResult());

    }

    @Test
    void testViolationCounterOnMulti() {

        PMDConfiguration config = newConfig();
        config.setThreads(2);

        GlobalAnalysisListener.ViolationCounterListener listener = new GlobalAnalysisListener.ViolationCounterListener();

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
        InternalApiBridgeForTestsOnly.setAnalysisCache(config, mockCache);

        MyFooRule rule = new MyFooRule();
        runPmd(config, GlobalAnalysisListener.noop(), rule);

        verify(mockCache).checkValidity(any(), any(), any());
        verify(mockCache, times(1)).persist();
        verify(mockCache, times(NUM_DATA_SOURCES)).isUpToDate(any());
    }

    @Test
    void testCacheWithFailure() throws Exception {

        PMDConfiguration config = newConfig();
        AnalysisCache mockCache = spy(NoopAnalysisCache.class);
        InternalApiBridgeForTestsOnly.setAnalysisCache(config, mockCache);

        BrokenRule rule = new BrokenRule();  // the broken rule throws
        runPmd(config, GlobalAnalysisListener.noop(), rule);

        // cache methods are called regardless
        verify(mockCache).checkValidity(any(), any(), any());
        verify(mockCache, times(1)).persist();
        verify(mockCache, times(NUM_DATA_SOURCES)).isUpToDate(any());
    }

    @Test
    void testCacheWithPropagatedException() throws Exception {

        PMDConfiguration config = newConfig();
        AnalysisCache mockCache = spy(NoopAnalysisCache.class);
        InternalApiBridgeForTestsOnly.setAnalysisCache(config, mockCache);

        BrokenRule rule = new BrokenRule();  // the broken rule throws
        // now the exception should be propagated
        GlobalAnalysisListener listener = GlobalAnalysisListener.exceptionThrower();
        FileAnalysisException exception = assertThrows(FileAnalysisException.class, () -> {
            runPmd(config, listener, rule);
        });

        assertEquals("fname1.dummy", exception.getFileId().getOriginalPath());

        // cache methods are called regardless
        verify(mockCache).checkValidity(any(), any(), any());
        verify(mockCache, times(1)).persist();
        verify(mockCache, times(1)).isUpToDate(any());
    }

    @NonNull
    private PMDConfiguration newConfig() {
        PMDConfiguration config = new PMDConfiguration();
        config.setAnalysisCacheLocation(null);
        config.setIgnoreIncrementalAnalysis(true);
        config.setThreads(0); // no multithreading for this test
        return config;
    }

    private void runPmd(PMDConfiguration config, GlobalAnalysisListener listener, Rule rule) {
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.files().addSourceFile(FileId.fromPathLikeString("fname1.dummy"), "abc");
            pmd.files().addSourceFile(FileId.fromPathLikeString("fname2.dummy"), "abcd");
            pmd.files().addSourceFile(FileId.fromPathLikeString("fname21.dummy"), "abcd");
            pmd.addListener(listener);
            pmd.performAnalysis();
        }
    }


    public static class MyFooRule extends FooRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            if (node.getTextDocument().getFileId().getFileName().contains("1")) {
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

    @Test
    void teeShouldForwardAllEventsSingleListeners() throws Exception {
        GlobalAnalysisListener mockListener1 = createMockListener();
        GlobalAnalysisListener teed = GlobalAnalysisListener.tee(Arrays.asList(mockListener1));

        teed.initializer();
        teed.startFileAnalysis(null);
        teed.onConfigError(null);
        teed.close();

        verifyMethods(mockListener1);
        Mockito.verifyNoMoreInteractions(mockListener1);
    }

    @Test
    void teeShouldForwardAllEventsMultipleListeners() throws Exception {
        GlobalAnalysisListener mockListener1 = createMockListener();
        GlobalAnalysisListener mockListener2 = createMockListener();
        GlobalAnalysisListener teed = GlobalAnalysisListener.tee(Arrays.asList(mockListener1, mockListener2));

        teed.initializer();
        teed.startFileAnalysis(null);
        teed.onConfigError(null);
        teed.close();

        verifyMethods(mockListener1);
        verifyMethods(mockListener2);
        Mockito.verifyNoMoreInteractions(mockListener1, mockListener2);
    }

    private GlobalAnalysisListener createMockListener() {
        GlobalAnalysisListener mockListener = Mockito.mock(GlobalAnalysisListener.class);
        Mockito.when(mockListener.initializer()).thenReturn(ListenerInitializer.noop());
        Mockito.when(mockListener.startFileAnalysis(Mockito.any())).thenReturn(FileAnalysisListener.noop());
        return mockListener;
    }

    private void verifyMethods(GlobalAnalysisListener listener) throws Exception {
        Mockito.verify(listener, Mockito.times(1)).initializer();
        Mockito.verify(listener, Mockito.times(1)).startFileAnalysis(null);
        Mockito.verify(listener, Mockito.times(1)).onConfigError(null);
        Mockito.verify(listener, Mockito.times(1)).close();
    }
}
