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

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.TextFile;

class ZipFileTest {

    private static final String ZIP_PATH = "src/test/resources/net/sourceforge/pmd/cli/zipWithSources.zip";
    private final Path zipPath = Paths.get(ZIP_PATH);

    @Test
    void testZipFile() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.addInputPath(zipPath);
        // no relativizeRoot paths configured -> we use the relative path
        String reportPath = zipPath.toString();
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo(reportPath + "!/otherSrc/somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo(reportPath + "!/src/somefile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo(reportPath + "!/src/somefile1.dummy"));
        }
    }

    @Test
    void testZipFileRelativizeWith() {
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
    void testZipFileRelativizeWithRoot() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.addInputPath(zipPath);
        // this configures "/" as the relativizeRoot -> result are absolute paths
        conf.addRelativizeRoot(zipPath.toAbsolutePath().getRoot());
        String reportPath = zipPath.toAbsolutePath().toString();
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getDisplayName(), equalTo(reportPath + "!/otherSrc/somefile.dummy"));
            assertThat(files.get(1).getDisplayName(), equalTo(reportPath + "!/src/somefile.dummy"));
            assertThat(files.get(2).getDisplayName(), equalTo(reportPath + "!/src/somefile1.dummy"));
        }
    }

}
