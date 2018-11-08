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
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.scala.cpd.ScalaTokenizer;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

public class ScalaTokenizerTest extends AbstractTokenizerTest {

    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private static final String FILENAME = "sample-LiftActor.scala";

    private File tempFile;

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        createTempFileOnDisk();

        this.tokenizer = new ScalaTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.FileCodeLoader(tempFile, "UTF-8"));
    }

    private void createTempFileOnDisk() throws IOException {
        this.tempFile = File.createTempFile("scala-tokenizer-test-", ".scala");
        FileUtils.writeStringToFile(tempFile, getSampleCode(), ENCODING);
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(ScalaTokenizer.class.getResourceAsStream(FILENAME), ENCODING);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 2591;
        super.tokenizeTest();
    }

    @After
    public void cleanUp() {
        FileUtils.deleteQuietly(this.tempFile);
        this.tempFile = null;
    }
}
