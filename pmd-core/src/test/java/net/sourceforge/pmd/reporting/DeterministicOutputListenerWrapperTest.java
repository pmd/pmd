/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.renderers.AbstractIncrementingRenderer;

class DeterministicOutputListenerWrapperTest {

    // repeat the test several times to make sure it isn't succeeding by chance
    @RepeatedTest(10)
    void testDeterministicOutputListener() {
        PMDConfiguration config = new PMDConfiguration();
        config.setAnalysisCacheLocation(null);
        config.setIgnoreIncrementalAnalysis(true);
        // Set threads to a "large" value to get sort of
        // random order of file processing
        config.setThreads(6);

        MyRuleReportingAlways mockrule = new MyRuleReportingAlways();

        MyRendererSpy myRenderer = new MyRendererSpy();

        List<FileId> fileIds = new ArrayList<>();
        final int numFiles = 100;
        for (int i = 0; i < numFiles; i++) {
            fileIds.add(FileId.fromPathLikeString("file" + i + ".dummy"));
        }
        Collections.shuffle(fileIds);

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(mockrule));
            pmd.addRenderer(myRenderer);

            for (FileId fileId : fileIds) {
                pmd.files().addSourceFile(fileId, "aoeuru uaoeru");
            }

            pmd.performAnalysis();
        }
        fileIds.sort(Comparator.naturalOrder());

        // Unless this assumption is true, the test is not testing anything
        Assumptions.assumeFalse(mockrule.fileIds.equals(fileIds),
                                "Order of application of rules was same as sorted by chance");

        // Assert that the renderer observed each file id in sorted order
        assertEquals(numFiles, myRenderer.fileIds.size());
        assertEquals(fileIds, myRenderer.fileIds);

    }


    /**
     * When the parallel analysis jobs are not joined successfully, some file
     * reports never reach this wrapper. Closing it in that state must surface
     * a diagnosis that points at the real problem (the missing/unjoined files),
     * not a bare {@code AssertionError} with no context.
     *
     * <p>This test drives the wrapper directly: two files are declared, but
     * only the report of the <em>second</em> file is ever delivered (so the
     * buffer keeps the first file's slot and {@code nextToOutput} never
     * catches up). Closing the wrapper must describe exactly which files were
     * never processed.
     */
    @Test
    void closeNamesUnprocessedFilesWhenReportsAreMissing() throws Exception {
        FileId file0 = FileId.fromPathLikeString("file0.dummy");
        FileId file1 = FileId.fromPathLikeString("file1.dummy");
        LanguageVersion lv = DummyLanguageModule.getInstance().getDefaultVersion();

        GlobalAnalysisListener recording = new GlobalAnalysisListener() {
            @Override
            public FileAnalysisListener startFileAnalysis(TextFile file) {
                return FileAnalysisListener.noop();
            }

            @Override
            public void close() {
                // nothing to close
            }
        };

        DeterministicOutputListenerWrapper wrapper = new DeterministicOutputListenerWrapper(recording);
        try (ListenerInitializer init = wrapper.initializer()) {
            init.setNumberOfFilesToAnalyze(2);
            init.setFilesToAnalyze(Arrays.asList(file0, file1));
        }

        // only file1's report arrives, out of order -> file0 is left buffered
        wrapper.startFileAnalysis(TextFile.forCharSeq("", file1, lv)).close();

        AssertionError error = assertThrows(AssertionError.class, wrapper::close);
        // The message must name the files that were never processed, otherwise
        // this assertion masks the real cause of the missing reports.
        assertTrue(error.getMessage().contains("file0.dummy")
                       && error.getMessage().contains("file1.dummy"),
                   "Expected the error to name the unprocessed files, got: " + error.getMessage());
    }

    static class MyRuleReportingAlways extends FooRule {

        /** This can be used to check that the order is random. */
        List<FileId> fileIds = new ArrayList<>();

        @Override
        public void apply(Node node, RuleContext ctx) {
            ctx.addViolation(node);
            synchronized (this) {
                fileIds.add(node.getTextDocument().getFileId());
            }
        }

        @Override
        public Rule deepCopy() {
            return this;
        }
    }

    static class MyRendererSpy extends AbstractIncrementingRenderer {

        List<FileId> fileIds = new ArrayList<>();

        MyRendererSpy() {
            super("rendererspy", "description");
            setWriter(new PrintWriter(System.out));
        }

        @Override
        public void renderFileViolations(Iterator<RuleViolation> violations) {
            fileIds.add(violations.next().getFileId());
        }

        @Override
        public String defaultFileExtension() {
            return "";
        }
    }


}
