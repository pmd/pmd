/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMDVersion;

public class SourceDistributionIT {
    private static final String BASE_PATH = "pmd-src-" + PMDVersion.VERSION;
    private static final Pattern GPG_PATTERN = Pattern.compile(Pattern.quote(BASE_PATH + "/.travis/") + ".+\\.[gG][pP][gG]");

    private File getSourceDistribution() {
        return new File(".", "target/" + BASE_PATH + ".zip");
    }

    @Test
    public void testFileExistence() {
        assertTrue(getSourceDistribution().exists());
    }

    @Test
    public void verifyExclusions() throws Exception {
        Set<String> exclusions = new HashSet<>();
        exclusions.add(BASE_PATH + "/.travis/secrets.tar");
        exclusions.add(BASE_PATH + "/.travis/id_rsa");
        List<String> files = ZipFileExtractor.readZipFile(getSourceDistribution().toPath());

        for (String file : files) {
            Assert.assertFalse("File " + file + " must not be included", exclusions.contains(file)
                    || GPG_PATTERN.matcher(file).matches());
        }
    }
}
