/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static net.sourceforge.pmd.internal.util.FileCollectionUtil.collectFileList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.log.MessageReporter;

class PMDFilelistTest {

    private static final Path RESOURCES = Paths.get("src/test/resources/net/sourceforge/pmd/cli/");

    private @NonNull FileCollector newCollector() {
        return FileCollector.newCollector(new LanguageVersionDiscoverer(LanguageRegistry.PMD), MessageReporter.quiet());
    }

    @Test
    void testGetApplicableFiles() {
        FileCollector collector = newCollector();

        collectFileList(collector, RESOURCES.resolve("filelist.txt"));

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getFileId().getFileName(), equalTo("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getFileId().getFileName(), equalTo("somefile.dummy"));
    }

    @Test
    void testGetApplicableFilesMultipleLines() {
        FileCollector collector = newCollector();

        collectFileList(collector, RESOURCES.resolve("filelist2.txt"));

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        // note: the file has 3 entries, but one is duplicated, resulting in 2 individual files
        assertThat(applicableFiles, hasSize(2));
        assertFilenameIs(applicableFiles.get(0), "anotherfile.dummy");
        assertFilenameIs(applicableFiles.get(1), "somefile.dummy");
    }

    private static void assertFilenameIs(TextFile textFile, String suffix) {
        assertThat(textFile.getFileId().getFileName(), is(suffix));
    }

    @Test
    void testGetApplicableFilesWithIgnores() {
        FileCollector collector = newCollector();

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath(RESOURCES.resolve("filelist3.txt"));
        configuration.setIgnoreFilePath(RESOURCES.resolve("ignorelist.txt"));
        FileCollectionUtil.collectFiles(configuration, collector);

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertFilenameIs(applicableFiles.get(0), "somefile2.dummy");
        assertFilenameIs(applicableFiles.get(1), "somefile4.dummy");
    }

    @Test
    void testRelativizeWith() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist2.txt"));
        conf.addRelativizeRoot(Paths.get("src/test/resources"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(2));
            assertHasName(files.get(0), IOUtil.normalizePath("net/sourceforge/pmd/cli/src/anotherfile.dummy"), pmd);
            assertHasName(files.get(1), IOUtil.normalizePath("net/sourceforge/pmd/cli/src/somefile.dummy"), pmd);
        }
    }

    public static void assertHasName(TextFile textFile, String expected, PmdAnalysis pmd) {
        assertThat(pmd.fileNameRenderer().getDisplayName(textFile), equalTo(expected));
    }

    @Test
    void testRelativizeWithOtherDir() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist4.txt"));
        conf.addRelativizeRoot(RESOURCES.resolve("src"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertHasName(files.get(0), ".." + IOUtil.normalizePath("/otherSrc/somefile.dummy"), pmd);
            assertHasName(files.get(1), "anotherfile.dummy", pmd);
            assertHasName(files.get(2), "somefile.dummy", pmd);
        }
    }

    @Test
    void testRelativizeWithSeveralDirs() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist4.txt"));
        conf.addRelativizeRoot(RESOURCES.resolve("src"));
        conf.addRelativizeRoot(RESOURCES.resolve("otherSrc"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertHasName(files.get(0), "somefile.dummy", pmd);
            assertHasName(files.get(1), "anotherfile.dummy", pmd);
            assertHasName(files.get(2), "somefile.dummy", pmd);
        }
    }

    @Test
    void testUseAbsolutePaths() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist4.txt"));
        conf.addRelativizeRoot(RESOURCES.toAbsolutePath().getRoot());
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertHasName(files.get(0), RESOURCES.resolve("otherSrc/somefile.dummy").toAbsolutePath().toString(), pmd);
            assertHasName(files.get(1), RESOURCES.resolve("src/anotherfile.dummy").toAbsolutePath().toString(), pmd);
            assertHasName(files.get(2), RESOURCES.resolve("src/somefile.dummy").toAbsolutePath().toString(), pmd);
        }
    }


    @Test
    void testGetApplicableFilesWithDirAndIgnores() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.addInputPath(RESOURCES.resolve("src"));
        configuration.setIgnoreFilePath(RESOURCES.resolve("ignorelist.txt"));

        FileCollector collector = newCollector();
        FileCollectionUtil.collectFiles(configuration, collector);

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(4));
        assertFilenameIs(applicableFiles.get(0), "anotherfile.dummy");
        assertFilenameIs(applicableFiles.get(1), "somefile.dummy");
        assertFilenameIs(applicableFiles.get(2), "somefile2.dummy");
        assertFilenameIs(applicableFiles.get(3), "somefile4.dummy");
    }

}
