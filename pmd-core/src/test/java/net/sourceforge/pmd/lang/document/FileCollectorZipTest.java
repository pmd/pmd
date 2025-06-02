/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.lang.document.FileCollectorTest.assertHasName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.internal.util.IOUtil;

class FileCollectorZipTest {

    private static final String ZIP_PATH = "src/test/resources/net/sourceforge/pmd/lang/document/filecollectorziptest/zipWithSources.zip";
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
            assertHasName(files.get(0), reportPath + "!/otherSrc/somefile.dummy", pmd);
            assertHasName(files.get(1), reportPath + "!/src/somefile.dummy", pmd);
            assertHasName(files.get(2), reportPath + "!/src/somefile1.dummy", pmd);
        }
    }

    @Test
    void testZipFileIds() throws IOException {
        PMDConfiguration conf = new PMDConfiguration();
        // no relativizeRoot paths configured -> we use the relative path
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            pmd.files().addZipFileWithContent(zipPath);
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertThat(files.get(0).getFileId().getUriString(),
                       equalTo("jar:" + zipPath.toUri() + "!/otherSrc/somefile.dummy"));

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
            String baseZipPath = IOUtil.normalizePath("net/sourceforge/pmd/lang/document/filecollectorziptest/zipWithSources.zip");
            assertHasName(files.get(0), baseZipPath + "!/otherSrc/somefile.dummy", pmd);
            assertHasName(files.get(1), baseZipPath + "!/src/somefile.dummy", pmd);
            assertHasName(files.get(2), baseZipPath + "!/src/somefile1.dummy", pmd);
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
            assertEquals("/otherSrc/somefile.dummy", files.get(0).getFileId().getAbsolutePath());
            URI zipUri = zipPath.toUri();
            assertEquals("jar:" + zipUri + "!/otherSrc/somefile.dummy", files.get(0).getFileId().getUriString());
            assertHasName(files.get(0), reportPath + "!/otherSrc/somefile.dummy", pmd);
            assertHasName(files.get(1), reportPath + "!/src/somefile.dummy", pmd);
            assertHasName(files.get(2), reportPath + "!/src/somefile1.dummy", pmd);
        }
    }

}
