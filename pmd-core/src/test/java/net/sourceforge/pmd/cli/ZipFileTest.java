/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.IOUtil;

public class ZipFileTest {

    private static final String ZIP_PATH = "src/test/resources/net/sourceforge/pmd/cli/zipWithSources.zip";
    private final Path zipPath = Paths.get(ZIP_PATH);

    @Test
    public void testZipFile() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.addInputPath(zipPath);
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo(IOUtil.normalizePath(ZIP_PATH) + "!/otherSrc/somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo(IOUtil.normalizePath(ZIP_PATH) + "!/src/somefile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo(IOUtil.normalizePath(ZIP_PATH) + "!/src/somefile1.dummy"));
        }
    }

    @Test
    public void testZipFileRelativizeWith() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.addInputPath(zipPath);
        conf.addRelativizeRoot(Paths.get("src/test/resources"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            String baseZipPath = IOUtil.normalizePath("net/sourceforge/pmd/cli/zipWithSources.zip");
            assertThat(files.get(0).getDisplayName(), equalTo(baseZipPath + "!/otherSrc/somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo(baseZipPath + "!/src/somefile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo(baseZipPath + "!/src/somefile1.dummy"));
        }
    }

    @Test
    public void testZipFileRelativizeWithRoot() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.addInputPath(zipPath);
        conf.addRelativizeRoot(zipPath.toAbsolutePath().getRoot());
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo(zipPath.toAbsolutePath() + "!/otherSrc/somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo(zipPath.toAbsolutePath() + "!/src/somefile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo(zipPath.toAbsolutePath() + "!/src/somefile1.dummy"));
        }
    }

}
