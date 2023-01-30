/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
import net.sourceforge.pmd.util.log.internal.NoopReporter;

class PMDFilelistTest {
    private static final String RESOURCE_PREFIX = "src/test/resources/net/sourceforge/pmd/cli/";

    private @NonNull FileCollector newCollector() {
        return FileCollector.newCollector(new LanguageVersionDiscoverer(LanguageRegistry.PMD), new NoopReporter());
    }

    private static void collectFileList(FileCollector collector, String x) {
        FileCollectionUtil.collectFileList(collector, Paths.get(x));
    }

    @Test
    void testGetApplicableFiles() {
        FileCollector collector = newCollector();

        collectFileList(collector, RESOURCE_PREFIX + "filelist.txt");

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile.dummy"));
    }

    @Test
    void testGetApplicableFilesMultipleLines() {
        FileCollector collector = newCollector();

        collectFileList(collector, RESOURCE_PREFIX + "filelist2.txt");

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        // note: the file has 3 entries, but one is duplicated, resulting in 2 individual files
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile.dummy"));
    }

    @Test
    void testGetApplicableFilesWithIgnores() {
        FileCollector collector = newCollector();

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath(RESOURCE_PREFIX + "filelist3.txt");
        configuration.setIgnoreFilePath(RESOURCE_PREFIX + "ignorelist.txt");
        FileCollectionUtil.collectFiles(configuration, collector);

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("somefile2.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile4.dummy"));
    }

    @Test
    void testRelativizeWith() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(Paths.get(RESOURCE_PREFIX + "filelist2.txt"));
        conf.addRelativizeRoot(Paths.get("src/test/resources"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(2));
            assertThat(files.get(0).getDisplayName(), equalTo(IOUtil.normalizePath("net/sourceforge/pmd/cli/src/anotherfile.dummy")));
            assertThat(files.get(1).getDisplayName(), equalTo(IOUtil.normalizePath("net/sourceforge/pmd/cli/src/somefile.dummy")));
        }
    }

    @Test
    void testRelativizeWithOtherDir() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(Paths.get(RESOURCE_PREFIX + "filelist4.txt"));
        conf.addRelativizeRoot(Paths.get(RESOURCE_PREFIX + "src"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo(".." + IOUtil.normalizePath("/otherSrc/somefile.dummy")));
            assertThat(files.get(1).getDisplayName(), equalTo("anotherfile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo("somefile.dummy"));
        }
    }

    @Test
    void testRelativizeWithSeveralDirs() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(Paths.get(RESOURCE_PREFIX + "filelist4.txt"));
        conf.addRelativizeRoot(Paths.get(RESOURCE_PREFIX + "src"));
        conf.addRelativizeRoot(Paths.get(RESOURCE_PREFIX + "otherSrc"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo("somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo("anotherfile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo("somefile.dummy"));
        }
    }

    @Test
    void testUseAbsolutePaths() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(Paths.get(RESOURCE_PREFIX + "filelist4.txt"));
        conf.addRelativizeRoot(Paths.get(RESOURCE_PREFIX).toAbsolutePath().getRoot());
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo(Paths.get(RESOURCE_PREFIX, "otherSrc", "somefile.dummy").toAbsolutePath().toString()));
            assertThat(files.get(1).getDisplayName(), equalTo(Paths.get(RESOURCE_PREFIX, "src", "anotherfile.dummy").toAbsolutePath().toString()));
            assertThat(files.get(2).getDisplayName(), equalTo(Paths.get(RESOURCE_PREFIX, "src", "somefile.dummy").toAbsolutePath().toString()));
        }
    }


    @Test
    void testGetApplicableFilesWithDirAndIgnores() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths(RESOURCE_PREFIX + "src");
        configuration.setIgnoreFilePath(RESOURCE_PREFIX + "ignorelist.txt");

        FileCollector collector = newCollector();
        FileCollectionUtil.collectFiles(configuration, collector);

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(4));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile.dummy"));
        assertThat(applicableFiles.get(2).getPathId(), endsWith("somefile2.dummy"));
        assertThat(applicableFiles.get(3).getPathId(), endsWith("somefile4.dummy"));
    }

}
