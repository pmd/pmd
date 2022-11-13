/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.datasource.DataSource;

public class PMDFilelistTest {

    private final Set<Language> languages = new HashSet<Language>(Arrays.asList(new DummyLanguageModule()));

    @Test
    public void testGetApplicableFiles() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist.txt");

        List<DataSource> applicableFiles = PMD.getApplicableFiles(configuration, languages);
        Assert.assertEquals(2, applicableFiles.size());
        assertThat(applicableFiles.get(0).getNiceFileName(false, ""), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getNiceFileName(false, ""), endsWith("somefile.dummy"));
    }

    @Test
    public void testGetApplicableFilesMultipleLines() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist2.txt");

        List<DataSource> applicableFiles = PMD.getApplicableFiles(configuration, languages);
        Assert.assertEquals(2, applicableFiles.size());
        assertThat(applicableFiles.get(0).getNiceFileName(false, ""), endsWith("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getNiceFileName(false, ""), endsWith("somefile.dummy"));
    }

    @Test
    public void testRelativizeWith() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(Paths.get("src/test/resources/net/sourceforge/pmd/cli/filelist2.txt"));
        conf.addRelativizeRoot(Paths.get("src/test/resources"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(2));
            assertThat(files.get(0).getDisplayName(), equalTo("net/sourceforge/pmd/cli/src/anotherfile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo("net/sourceforge/pmd/cli/src/somefile.dummy"));
        }
    }

    @Test
    public void testRelativizeWithOtherDir() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(Paths.get("src/test/resources/net/sourceforge/pmd/cli/filelist4.txt"));
        conf.addRelativizeRoot(Paths.get("src/test/resources/net/sourceforge/pmd/cli/src"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo("../otherSrc/somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo("anotherfile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo("somefile.dummy"));
        }
    }

    @Test
    public void testRelativizeWithSeveralDirs() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(Paths.get("src/test/resources/net/sourceforge/pmd/cli/filelist4.txt"));
        conf.addRelativizeRoot(Paths.get("src/test/resources/net/sourceforge/pmd/cli/src"));
        conf.addRelativizeRoot(Paths.get("src/test/resources/net/sourceforge/pmd/cli/otherSrc"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo("somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo("anotherfile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo("somefile.dummy"));
        }
    }

    @Test
    public void testGetApplicatbleFilesWithIgnores() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist3.txt");
        configuration.setIgnoreFilePath("src/test/resources/net/sourceforge/pmd/cli/ignorelist.txt");

        List<DataSource> applicableFiles = PMD.getApplicableFiles(configuration, languages);
        Assert.assertEquals(2, applicableFiles.size());
        Assert.assertTrue(applicableFiles.get(0).getNiceFileName(false, "").endsWith("somefile2.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getNiceFileName(false, "").endsWith("somefile4.dummy"));
    }

    @Test
    public void testGetApplicatbleFilesWithDirAndIgnores() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths("src/test/resources/net/sourceforge/pmd/cli/src");
        configuration.setIgnoreFilePath("src/test/resources/net/sourceforge/pmd/cli/ignorelist.txt");

        List<DataSource> applicableFiles = PMD.getApplicableFiles(configuration, languages);
        Assert.assertEquals(4, applicableFiles.size());
        Collections.sort(applicableFiles, new Comparator<DataSource>() {
            @Override
            public int compare(DataSource o1, DataSource o2) {
                if (o1 == null && o2 != null) {
                    return -1;
                } else if (o1 != null && o2 == null) {
                    return 1;
                } else {
                    return o1.getNiceFileName(false, "").compareTo(o2.getNiceFileName(false, ""));
                }
            }
        });
        Assert.assertTrue(applicableFiles.get(0).getNiceFileName(false, "").endsWith("anotherfile.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getNiceFileName(false, "").endsWith("somefile.dummy"));
        Assert.assertTrue(applicableFiles.get(2).getNiceFileName(false, "").endsWith("somefile2.dummy"));
        Assert.assertTrue(applicableFiles.get(3).getNiceFileName(false, "").endsWith("somefile4.dummy"));
    }
}
