/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.util.datasource.DataSource;

public class PMDFilelistTest {
    @Test
    public void testGetApplicableFiles() {
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist.txt");

        List<DataSource> applicableFiles = PMD.getApplicableFiles(configuration, languages);
        Assert.assertEquals(2, applicableFiles.size());
        Assert.assertTrue(applicableFiles.get(0).getNiceFileName(false, "").endsWith("somefile.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getNiceFileName(false, "").endsWith("anotherfile.dummy"));
    }

    @Test
    public void testGetApplicableFilesMultipleLines() {
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist2.txt");

        List<DataSource> applicableFiles = PMD.getApplicableFiles(configuration, languages);
        Assert.assertEquals(3, applicableFiles.size());
        Assert.assertTrue(applicableFiles.get(0).getNiceFileName(false, "").endsWith("somefile.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getNiceFileName(false, "").endsWith("anotherfile.dummy"));
        Assert.assertTrue(applicableFiles.get(2).getNiceFileName(false, "").endsWith("somefile.dummy"));
    }

    @Test
    public void testGetApplicatbleFilesWithIgnores() {
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

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
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

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
