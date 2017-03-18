/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

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

}
