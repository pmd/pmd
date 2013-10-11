/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Test;

public class XPathCLITest {

    @Test
    public void runXPath() throws Exception {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        try {
            XPathCLI.main(new String[] {
                    "-xpath",
                    "//ClassOrInterfaceDeclaration",
                    "-filename",
                    "src/test/java/net/sourceforge/pmd/cli/XPathCLITest.java"
            });
            System.out.flush();
        } finally {
            System.setOut(oldOut);
        }

        Assert.assertTrue(output.toString("UTF-8").startsWith("Match at line "));
    }
}
