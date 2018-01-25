/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DocumentOperationsApplierForNonOverlappingRegionsWithDocumentFileTest {

    private static final String FILE_PATH = "psvm.java";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File temporaryFile;

    private DocumentOperationsApplierForNonOverlappingRegions applier;

    @Before
    public void setUpTemporaryFiles() throws IOException {
        temporaryFile = temporaryFolder.newFile(FILE_PATH);
    }

    @Test
    public void insertAtStartOfTheDocumentShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "public "));

            applier.apply();
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public static void main(String[] args) {}", actualContent);
        }
    }

    private byte[] readAllBytes(final FileInputStream stream) throws IOException {
        final int defaultBufferSize = 8192;
        final int maxBufferSize = Integer.MAX_VALUE - 8;

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
    public void removeTokenShouldSucceed() throws IOException {
        writeContentToTemporaryFile("public static void main(String[] args) {}");

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 7, 13));

            applier.apply();
        }

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public  void main(String[] args) {}", actualContent);
        }
    }

    @Test
    public void insertAndRemoveTokensShouldSucceed() throws IOException {
        final String code = "static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);

        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "public "));
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 17, 23));

            applier.apply();
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
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);

            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "public "));
            applier.addDocumentOperation(new InsertDocumentOperation(0, 0, "static "));
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 0, 4));
            applier.addDocumentOperation(new InsertDocumentOperation(0, 10, "final "));
            applier.addDocumentOperation(new DeleteDocumentOperation(0, 0, 25, 27));

            applier.apply();
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
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);

            applier.addDocumentOperation(new ReplaceDocumentOperation(0, 0, 0, "int".length(), "void"));

            applier.apply();
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

        final List<DocumentOperation> documentOperations = new LinkedList<>();
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 0, "int".length(), "void"));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 4, 4 + "main".length(), "foo"));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 9, 9 + "String".length(), "CharSequence"));

        shuffleAndApplyOperations(documentOperations);

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("void foo(CharSequence[] args) {}", actualContent);
        }
    }

    private void shuffleAndApplyOperations(List<DocumentOperation> documentOperations) throws IOException {
        try (DocumentFile documentFile = new DocumentFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);

            Collections.shuffle(documentOperations);

            for (final DocumentOperation operation : documentOperations) {
                applier.addDocumentOperation(operation);
            }

            applier.apply();
        }
    }

    @Test
    public void insertDeleteAndReplaceVariousTokensShouldSucceed() throws IOException {
        final String code = "static int main(CharSequence[] args) {}";
        writeContentToTemporaryFile(code);

        final List<DocumentOperation> documentOperations = new LinkedList<>();
        documentOperations.add(new InsertDocumentOperation(0, 0, "public"));
        documentOperations.add(new DeleteDocumentOperation(0, 0, 0, 6));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 7, 7 + "int".length(), "void"));
        documentOperations.add(new InsertDocumentOperation(0, 16, "final "));
        documentOperations.add(new ReplaceDocumentOperation(0, 0, 16, 16 + "CharSequence".length(), "String"));

        shuffleAndApplyOperations(documentOperations);

        try (FileInputStream stream = new FileInputStream(temporaryFile)) {
            final String actualContent = new String(readAllBytes(stream));
            assertEquals("public void main(final String[] args) {}", actualContent);
        }
    }

    private void writeContentToTemporaryFile(final String content) throws IOException {
        try (FileWriter writer = new FileWriter(temporaryFile)) {
            writer.write(content);
        }
    }
}
