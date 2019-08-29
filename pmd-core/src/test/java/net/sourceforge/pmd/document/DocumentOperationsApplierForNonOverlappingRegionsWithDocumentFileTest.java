/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static net.sourceforge.pmd.document.DocumentOperation.createDelete;
import static net.sourceforge.pmd.document.DocumentOperation.createInsert;
import static net.sourceforge.pmd.document.DocumentOperation.createReplace;
import static org.junit.Assert.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private Path temporaryFile;

    private DocumentOperationsApplierForNonOverlappingRegions applier;

    @Before
    public void setUpTemporaryFiles() throws IOException {
        temporaryFile = temporaryFolder.newFile(FILE_PATH).toPath();
    }

    @Test
    public void insertAtStartOfTheDocumentShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(createInsert(0, 0, "public "));

            applier.apply();
        }

        assertFinalFileIs("public static void main(String[] args) {}");
    }

    @Test
    public void removeTokenShouldSucceed() throws IOException {
        writeContentToTemporaryFile("public static void main(String[] args) {}");

        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(createDelete(0, 0, 7, 13));

            applier.apply();
        }

        assertFinalFileIs("public  void main(String[] args) {}");
    }

    private void assertFinalFileIs(String s) throws IOException {
        final String actualContent = new String(Files.readAllBytes(temporaryFile));
        assertEquals(s, actualContent);
    }

    @Test
    public void insertAndRemoveTokensShouldSucceed() throws IOException {
        final String code = "static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);
            applier.addDocumentOperation(createInsert(0, 0, "public "));
            applier.addDocumentOperation(createDelete(0, 0, 17, 23));

            applier.apply();
        }

        assertFinalFileIs("public static void main(String[] args) {}");
    }

    @Test
    public void insertAndDeleteVariousTokensShouldSucceed() throws IOException {
        final String code = "void main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);

            applier.addDocumentOperation(createInsert(0, 0, "public "));
            applier.addDocumentOperation(createInsert(0, 0, "static "));
            applier.addDocumentOperation(createDelete(0, 0, 0, 4));
            applier.addDocumentOperation(createInsert(0, 10, "final "));
            applier.addDocumentOperation(createDelete(0, 0, 25, 27));

            applier.apply();
        }

        assertFinalFileIs("public static  main(final String[] args) ");
    }

    @Test
    public void replaceATokenShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            applier = new DocumentOperationsApplierForNonOverlappingRegions(documentFile);

            applier.addDocumentOperation(createReplace(0, 0, 0, "int".length(), "void"));

            applier.apply();
        }

        assertFinalFileIs("void main(String[] args) {}");
    }

    @Test
    public void replaceVariousTokensShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);

        final List<DocumentOperation> documentOperations = new LinkedList<>();
        documentOperations.add(createReplace(0, 0, 0, "int".length(), "void"));
        documentOperations.add(createReplace(0, 0, 4, 4 + "main".length(), "foo"));
        documentOperations.add(createReplace(0, 0, 9, 9 + "String".length(), "CharSequence"));

        shuffleAndApplyOperations(documentOperations);

        assertFinalFileIs("void foo(CharSequence[] args) {}");
    }

    private void shuffleAndApplyOperations(List<DocumentOperation> documentOperations) throws IOException {
        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
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
        documentOperations.add(createInsert(0, 0, "public"));
        documentOperations.add(createDelete(0, 0, 0, 6));
        documentOperations.add(createReplace(0, 0, 7, 7 + "int".length(), "void"));
        documentOperations.add(createInsert(0, 16, "final "));
        documentOperations.add(createReplace(0, 0, 16, 16 + "CharSequence".length(), "String"));

        shuffleAndApplyOperations(documentOperations);

        assertFinalFileIs("public void main(final String[] args) {}");
    }

    private void writeContentToTemporaryFile(final String content) throws IOException {
        try (FileWriter writer = new FileWriter(temporaryFile.toFile())) {
            writer.write(content);
        }
    }
}
