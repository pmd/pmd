/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;

import java.nio.file.Paths;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.log.internal.NoopReporter;

class PMDFilelistTest {

    private @NonNull FileCollector newCollector() {
        return FileCollector.newCollector(new LanguageVersionDiscoverer(LanguageRegistry.PMD), new NoopReporter());
    }

    private static void collectFileList(FileCollector collector, String x) {
        FileCollectionUtil.collectFileList(collector, Paths.get(x));
    }

    @Test
    void testGetApplicableFiles() {
        FileCollector collector = newCollector();

        collectFileList(collector, "src/test/resources/net/sourceforge/pmd/cli/filelist.txt");

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile.dummy"));
    }

    @Test
    void testGetApplicableFilesMultipleLines() {
        FileCollector collector = newCollector();

        collectFileList(collector, "src/test/resources/net/sourceforge/pmd/cli/filelist2.txt");

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(3));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile.dummy"));
        assertThat(applicableFiles.get(2).getPathId(), endsWith("somefile.dummy"));
    }

    @Test
    void testGetApplicableFilesWithIgnores() {
        FileCollector collector = newCollector();

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist3.txt");
        configuration.setIgnoreFilePath("src/test/resources/net/sourceforge/pmd/cli/ignorelist.txt");
        FileCollectionUtil.collectFiles(configuration, collector);

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("somefile2.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile4.dummy"));
    }

    @Test
    void testGetApplicableFilesWithDirAndIgnores() {

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths("src/test/resources/net/sourceforge/pmd/cli/src");
        configuration.setIgnoreFilePath("src/test/resources/net/sourceforge/pmd/cli/ignorelist.txt");

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
