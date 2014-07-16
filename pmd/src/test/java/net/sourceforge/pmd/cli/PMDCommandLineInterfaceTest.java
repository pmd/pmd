/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link PMDCommandLineInterface}
 */
public class PMDCommandLineInterfaceTest {

    @Test
    public void testProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "yahtml", "-P", "outputDir=output_folder",
                "-R", "java-empty" };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assert.assertEquals("output_folder", params.getProperties().getProperty("outputDir"));
    }
}
