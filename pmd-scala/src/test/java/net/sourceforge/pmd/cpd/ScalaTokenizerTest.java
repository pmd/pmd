/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.IOException;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import net.sourceforge.pmd.testframework.StreamUtil;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.scala.cpd.ScalaTokenizer;


public class ScalaTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "sample-LiftActor.scala";

    private File tempFile;

    @Before
    @Override
    public void buildTokenizer() {
        createTempFileOnDisk();

        this.tokenizer = new ScalaTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.FileCodeLoader(tempFile, "UTF-8"));
    }

    private void createTempFileOnDisk() {
        try {
            this.tempFile = File.createTempFile("scala-tokenizer-test-", ".scala");

            FileUtils.writeStringToFile(tempFile, getSampleCode(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Unable to create temporary file on disk for Scala tokenizer test", e);
        }
    }

    @Override
    public String getSampleCode() {
        return StreamUtil.toString(ScalaTokenizer.class.getResourceAsStream(FILENAME));
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ScalaTokenizerTest.class);
    }
}
