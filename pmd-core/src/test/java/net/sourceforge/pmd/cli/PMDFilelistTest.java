/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;

import java.io.IOException;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.log.internal.NoopReporter;

public class PMDFilelistTest {

    private static @NonNull FileCollector newCollector() {
        return FileCollector.newCollector(new LanguageVersionDiscoverer(), new NoopReporter());
    }

    @Test
    public void testGetApplicableFiles() throws IOException {
        FileCollector collector = newCollector();

        FileCollectionUtil.collectFileList(collector, "src/test/resources/net/sourceforge/pmd/cli/filelist.txt");

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile.dummy"));
    }

    @Test
    public void testGetApplicableFilesMultipleLines() throws IOException {
        FileCollector collector = newCollector();

        FileCollectionUtil.collectFileList(collector, "src/test/resources/net/sourceforge/pmd/cli/filelist2.txt");

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(3));
        assertThat(applicableFiles.get(0).getPathId(), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getPathId(), endsWith("somefile.dummy"));
        assertThat(applicableFiles.get(2).getPathId(), endsWith("somefile.dummy"));
    }

    @Test
    public void testGetApplicableFilesWithIgnores() throws IOException {
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
    public void testGetApplicableFilesWithDirAndIgnores() throws IOException {

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
