/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.cache.AnalysisCache;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener.ViolationCounterListener;
import net.sourceforge.pmd.util.document.TextFile;

public class GlobalListenerTest {

    static RuleSet mockRuleset(Rule rule) {
        return RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);
    }

    private final LanguageVersion dummyVersion = LanguageRegistry.getDefaultLanguage().getDefaultVersion();

    static final int NUM_DATA_SOURCES = 3;

    List<TextFile> mockDataSources() {
        return listOf(
            TextFile.forCharSeq("abc", "fname1.dummy", dummyVersion),
            TextFile.forCharSeq("abcd", "fname2.dummy", dummyVersion),
            TextFile.forCharSeq("abcd", "fname21.dummy", dummyVersion)
        );
    }

    @Test
    public void testViolationCounter() throws Exception {

        PMDConfiguration config = newConfig();

        ViolationCounterListener listener = new GlobalAnalysisListener.ViolationCounterListener();

        MyFooRule mockrule = Mockito.spy(MyFooRule.class);
        runPmd(config, listener, mockrule);

        Mockito.verify(mockrule, times(NUM_DATA_SOURCES)).apply(any(), any());
        assertEquals(2, (int) listener.getResult());

    }

    @Test
    public void testViolationCounterOnMulti() throws Exception {

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
    public void testAnalysisCache() throws Exception {

        PMDConfiguration config = newConfig();
        AnalysisCache mockCache = spy(NoopAnalysisCache.class);
        config.setAnalysisCache(mockCache);

        MyFooRule rule = new MyFooRule();
        runPmd(config, GlobalAnalysisListener.noop(), rule);

        verify(mockCache).checkValidity(any(), any());
        verify(mockCache).persist();
        verify(mockCache, times(NUM_DATA_SOURCES)).isUpToDate(any());
    }

    @Test
    public void testCacheWithFailure() throws Exception {

        PMDConfiguration config = newConfig();
        AnalysisCache mockCache = spy(NoopAnalysisCache.class);
        config.setAnalysisCache(mockCache);

        BrokenRule rule = new BrokenRule();  // the broken rule throws
        runPmd(config, GlobalAnalysisListener.noop(), rule);

        // cache methods are called regardless
        verify(mockCache).checkValidity(any(), any());
        verify(mockCache).persist();
        verify(mockCache, times(NUM_DATA_SOURCES)).isUpToDate(any());
    }

    @Test
    public void testCacheWithPropagatedException() throws Exception {

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
        verify(mockCache).persist();
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

    private void runPmd(PMDConfiguration config, GlobalAnalysisListener listener, Rule rule) throws Exception {
        try {
            PMD.processTextFiles(
                config,
                listOf(mockRuleset(rule)),
                mockDataSources(),
                listener
            );
        } finally {
            listener.close();
        }
    }


    public static class MyFooRule extends FooRule {

        @Override
        public void apply(Node node, RuleContext ctx) {
            if (node.getTextDocument().getDisplayName().contains("1")) {
                addViolation(ctx, node);
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
