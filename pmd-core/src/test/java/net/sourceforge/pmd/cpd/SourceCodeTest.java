/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.cpd.SourceCode.FileCodeLoader;

public class SourceCodeTest {
    private static final String BASE_RESOURCE_PATH = "src/test/resources/net/sourceforge/pmd/cpd/files/";

    private static final String SAMPLE_CODE = "Line 1\n" + "Line 2\n" + "Line 3\n" + "Line 4\n";

    @Test
    public void testSlice() {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(SAMPLE_CODE, "Foo.java"));
        assertEquals("Foo.java", sourceCode.getFileName());

        assertEquals("Line 1", sourceCode.getSlice(1, 1));
        assertEquals("Line 2", sourceCode.getSlice(2, 2));
        assertEquals("Line 1\nLine 2", sourceCode.getSlice(1, 2));

        sourceCode.getCodeBuffer(); // load into soft reference, must not change behavior
        assertEquals("Line 1\nLine 2", sourceCode.getSlice(1, 2));
    }

    @Test
    public void testEncodingDetectionFromBOM() throws Exception {
        FileCodeLoader loader = new SourceCode.FileCodeLoader(new File(BASE_RESOURCE_PATH + "file_with_utf8_bom.java"),
                "ISO-8859-1");

        // The encoding detection is done when the reader is created
        loader.getReader();
        assertEquals("UTF-8", loader.getEncoding());
    }

    @Test
    public void testEncodingIsNotChangedWhenThereIsNoBOM() throws Exception {
        FileCodeLoader loader = new SourceCode.FileCodeLoader(
                new File(BASE_RESOURCE_PATH + "file_with_ISO-8859-1_encoding.java"), "ISO-8859-1");

        // The encoding detection is done when the reader is created
        loader.getReader();
        assertEquals("ISO-8859-1", loader.getEncoding());
    }
}
