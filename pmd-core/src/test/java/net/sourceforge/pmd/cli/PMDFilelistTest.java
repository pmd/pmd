/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.document.io.TextFile;

public class PMDFilelistTest {
    @Test
    public void testGetApplicableFiles() throws IOException {
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist.txt");

        List<TextFile> applicableFiles = FileUtil.getApplicableFiles(configuration, languages);
        Assert.assertEquals(2, applicableFiles.size());
        Assert.assertTrue(applicableFiles.get(0).getPathId().endsWith("somefile.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getPathId().endsWith("anotherfile.dummy"));
    }

    @Test
    public void testGetApplicableFilesMultipleLines() throws IOException {
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist2.txt");

        List<TextFile> applicableFiles = FileUtil.getApplicableFiles(configuration, languages);
        Assert.assertEquals(3, applicableFiles.size());
        Assert.assertTrue(applicableFiles.get(0).getPathId().endsWith("somefile.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getPathId().endsWith("anotherfile.dummy"));
        Assert.assertTrue(applicableFiles.get(2).getPathId().endsWith("somefile.dummy"));
    }

    @Test
    public void testGetApplicatbleFilesWithIgnores() throws IOException {
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath("src/test/resources/net/sourceforge/pmd/cli/filelist3.txt");
        configuration.setIgnoreFilePath("src/test/resources/net/sourceforge/pmd/cli/ignorelist.txt");

        List<TextFile> applicableFiles = FileUtil.getApplicableFiles(configuration, languages);
        Assert.assertEquals(2, applicableFiles.size());
        Assert.assertTrue(applicableFiles.get(0).getPathId().endsWith("somefile2.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getPathId().endsWith("somefile4.dummy"));
    }

    @Test
    public void testGetApplicatbleFilesWithDirAndIgnores() throws IOException {
        Set<Language> languages = new HashSet<>();
        languages.add(new DummyLanguageModule());

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths("src/test/resources/net/sourceforge/pmd/cli/src");
        configuration.setIgnoreFilePath("src/test/resources/net/sourceforge/pmd/cli/ignorelist.txt");

        List<TextFile> applicableFiles = FileUtil.getApplicableFiles(configuration, languages);
        Assert.assertEquals(4, applicableFiles.size());
        applicableFiles.sort(Comparator.comparing(TextFile::getPathId));
        Assert.assertTrue(applicableFiles.get(0).getPathId().endsWith("anotherfile.dummy"));
        Assert.assertTrue(applicableFiles.get(1).getPathId().endsWith("somefile.dummy"));
        Assert.assertTrue(applicableFiles.get(2).getPathId().endsWith("somefile2.dummy"));
        Assert.assertTrue(applicableFiles.get(3).getPathId().endsWith("somefile4.dummy"));
    }
}
