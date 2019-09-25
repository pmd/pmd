/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

public class ScalaTokenizerTest extends AbstractTokenizerTest {

    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private static final String FILENAME = "/tokenizerFiles/sample-LiftActor.scala";

    private File tempFile;

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        createTempFileOnDisk();
        this.tokenizer = new ScalaTokenizer();
    }

    private void createTempFileOnDisk() throws IOException {
        this.tempFile = File.createTempFile("scala-tokenizer-test-", ".scala");
        FileUtils.writeStringToFile(tempFile, getSampleCode(), ENCODING);
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(FILENAME), ENCODING);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.sourceCode = new SourceCode(new SourceCode.FileCodeLoader(tempFile, "UTF-8"));
        this.expectedTokenCount = 2472;
        super.tokenizeTest();
    }

    @Test
    public void tokenizeFailTest() throws IOException {
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(
                "  object Main { "
                + " def main(args: Array[String]): Unit = { "
                + "  println(\"Hello, World!) " //unclosed string literal
                + " }"
                + "}"));
        try {
            super.tokenizeTest();
            Assert.fail();
        } catch (Exception e) {
            // intentional
        }
    }

    @After
    public void cleanUp() {
        FileUtils.deleteQuietly(this.tempFile);
        this.tempFile = null;
    }
}
