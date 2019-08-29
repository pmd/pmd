/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static net.sourceforge.pmd.document.TextRegion.newRegionByLine;
import static net.sourceforge.pmd.document.TextRegion.newRegionByOffset;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DocumentFileTest {

    private static final String FILE_PATH = "psvm.java";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File temporaryFile;

    @Before
    public void setUpTemporaryFiles() throws IOException {
        temporaryFile = temporaryFolder.newFile(FILE_PATH);
    }

    @Test
    public void insertAtStartOfTheFileShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(1, 1, "public ");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAtStartOfTheFileWithOffsetShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, "public ");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void shouldPreserveNewlines() throws IOException {
        final String testFileContent = IOUtils.toString(
                DocumentFileTest.class.getResource("ShouldPreserveNewlines.java"), StandardCharsets.UTF_8);
        writeContentToTemporaryFile(testFileContent);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, 0, "public ");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public " + testFileContent, actualContent);
        }
    }

    private byte[] readAllBytes(final FileInputStream stream) throws IOException {
        final int defaultBufferSize = 8192;
        final int maxBufferSize = 2147483639;

        byte[] buf = new byte[defaultBufferSize];
        int capacity = buf.length;
        int nread = 0;
        int n;
        while (true) {
            // read to EOF which may read more or less than initial buffer size
            while ((n = stream.read(buf, nread, capacity - nread)) > 0) {
                nread += n;
            }

            // if the last call to read returned -1, then we're done
            if (n < 0) {
                break;
            }

            // need to allocate a larger buffer
            if (capacity <= maxBufferSize - capacity) {
                capacity = capacity << 1;
            } else {
                if (capacity == maxBufferSize) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                capacity = maxBufferSize;
            }
            buf = Arrays.copyOf(buf, capacity);
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }

    @Test
    public void insertVariousTokensIntoTheFileShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(1, 1, "public ");
            documentFile.insert(17, "final ");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static void main(final String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAtTheEndOfTheFileShouldSucceed() throws IOException {
        final String code = "public static void main(String[] args)";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(code.length(), "{}");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static void main(String[] args){}", actualContent);
        }
    }

    @Test
    public void removeTokenShouldSucceed() throws IOException {
        final String code = "public static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.delete(newRegionByLine(1, 25, 1, 31));
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAndRemoveTokensShouldSucceed() throws IOException {
        final String code = "static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(1, 1, "public ");
            documentFile.delete(newRegionByOffset("static void main(".length(), "final ".length()));
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAndDeleteVariousTokensShouldSucceed() throws IOException {
        final String code = "void main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, "public ");
            documentFile.insert(0, "static ");
            // delete "void"
            documentFile.delete(newRegionByOffset(0, 4));
            documentFile.insert(10, "final ");
            // delete "{}"
            documentFile.delete(newRegionByOffset("void main(String[] args) ".length(), 2));
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static  main(final String[] args) ", actualContent);
        }
    }

    @Test
    public void replaceATokenShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.replace(newRegionByOffset(0, 3), "void");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void replaceVariousTokensShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.replace(newRegionByLine(1, 1, 1, 1 + "int".length()), "void");
            documentFile.replace(newRegionByLine(1, 1 + "int ".length(), 1, 1 + "int main".length()), "foo");
            documentFile.replace(newRegionByOffset("int main(".length(), "String".length()), "CharSequence");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("void foo(CharSequence[] args) {}", actualContent);
        }
    }

    @Test
    public void insertDeleteAndReplaceVariousTokensShouldSucceed() throws IOException {
        final String code = "static int main(CharSequence[] args) {}";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(1, 1, "public");
            // delete "static "
            documentFile.delete(newRegionByLine(1, 1, 1, 7));
            // replace "int"
            documentFile.replace(newRegionByLine(1, 8, 1, 8 + "int".length()), "void");
            documentFile.insert(1, 17, "final ");
            documentFile.replace(newRegionByLine(1, 17, 1, 17 + "CharSequence".length()), "String");
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public void main(final String[] args) {}", actualContent);
        }
    }
    private void writeContentToTemporaryFile(final String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

}
