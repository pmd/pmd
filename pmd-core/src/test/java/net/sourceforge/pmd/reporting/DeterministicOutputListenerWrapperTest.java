/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

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
     * a diagnosis that gives context information about the current status
     * (the missing and the buffered files), not a bare {@code AssertionError}
     * with no context.
     *
     * <p>This test drives the wrapper directly: two files are declared, but
     * only the report of the <em>second</em> file is ever delivered (so the
     * buffer keeps the first file's slot and {@code nextToOutput} never
     * catches up). Closing the wrapper must name the first file as never
     * delivered and the second file as still buffered.
     */
    @Test
    void closeNamesUnprocessedFilesWhenReportsAreMissing() throws Exception {
        FileId file0 = FileId.fromPathLikeString("file0.dummy");
        FileId file1 = FileId.fromPathLikeString("file1.dummy");
        LanguageVersion lv = DummyLanguageModule.getInstance().getDefaultVersion();

        DeterministicOutputListenerWrapper wrapper =
                new DeterministicOutputListenerWrapper(GlobalAnalysisListener.noop());
        try (ListenerInitializer init = wrapper.initializer()) {
            init.setNumberOfFilesToAnalyze(2);
            init.setFilesToAnalyze(Arrays.asList(file0, file1));
        }

        // only file1's report arrives, out of order -> file0 is left behind
        wrapper.startFileAnalysis(TextFile.forCharSeq("", file1, lv)).close();

        AssertionError error = assertThrows(AssertionError.class, wrapper::close);
        // The message must name the files that were never processed; this gives a
        // hint which files are problematic and may have caused an error.
        assertEquals("Closed listener but not all files have been processed:\n"
                + "  1 file(s) never delivered a report: [file0.dummy]\n"
                + "  1 file(s) analyzed but still buffered: [file1.dummy]", error.getMessage());
    }

    @Test
    void closeNamesUnprocessedFilesWhenReportsAreMissingMoreThanTenFiles() throws Exception {
        List<FileId> notAnalyzed = IntStream.range(0, 13)
                .boxed()
                .map(i -> FileId.fromPathLikeString(String.format("file%03d.dummy", i)))
                .collect(Collectors.toList());
        List<FileId> buffered = IntStream.range(13, 28)
                .boxed()
                .map(i -> FileId.fromPathLikeString(String.format("file%03d.dummy", i)))
                .collect(Collectors.toList());
        LanguageVersion lv = DummyLanguageModule.getInstance().getDefaultVersion();

        DeterministicOutputListenerWrapper wrapper =
                new DeterministicOutputListenerWrapper(GlobalAnalysisListener.noop());
        try (ListenerInitializer init = wrapper.initializer()) {
            List<FileId> allFiles = new ArrayList<>(notAnalyzed);
            allFiles.addAll(buffered);
            init.setNumberOfFilesToAnalyze(allFiles.size());
            init.setFilesToAnalyze(allFiles);
        }

        // only the reports of the buffered files arrive -> notAnalyzed is left behind
        for (FileId file : buffered) {
            wrapper.startFileAnalysis(TextFile.forCharSeq("", file, lv)).close();
        }

        AssertionError error = assertThrows(AssertionError.class, wrapper::close);
        // The message must name the files that were never processed; this gives a
        // hint which files are problematic and may have caused an error.
        assertEquals("Closed listener but not all files have been processed:\n"
                + "  13 file(s) never delivered a report: ["
                + "file000.dummy, file001.dummy, file002.dummy, file003.dummy, file004.dummy, "
                + "file005.dummy, file006.dummy, file007.dummy, file008.dummy, file009.dummy"
                + "] ... and 3 more\n"
                + "  15 file(s) analyzed but still buffered: ["
                + "file013.dummy, file014.dummy, file015.dummy, file016.dummy, file017.dummy, "
                + "file018.dummy, file019.dummy, file020.dummy, file021.dummy, file022.dummy"
                + "] ... and 5 more", error.getMessage());
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
            setWriter(new StringWriter());
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
