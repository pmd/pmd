/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DocumentFileTest {

    private static final String FILE_PATH = "psvm.java";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private Path temporaryFile;

    @Before
    public void setUpTemporaryFiles() throws IOException {
        temporaryFile = temporaryFolder.newFile(FILE_PATH).toPath();
    }

    @Test
    public void insertAtStartOfTheFileShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (MutableTextDocument documentFile = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(1, 1, "public ");
        }

        assertFinalFileIs("public static void main(String[] args) {}");
    }

    @Test
    public void insertAtStartOfTheFileWithOffsetShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (MutableTextDocument documentFile = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, "public ");
        }

        assertFinalFileIs("public static void main(String[] args) {}");
    }


    @Test
    public void shouldPreserveNewlinesLf() throws IOException {

        final String testFileContent =
            "class ShouldPreserveNewlines {\n"
            + "    public static void main(String[] args) {\n"
            + "        System.out.println(\"Test\");\n"
            + "    }\n"
            + "}\n"
            + "// note: multiple empty lines at the end\n"
            + "\n"
            + "\n";

        writeContentToTemporaryFile(testFileContent);

        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, "public ");
        }

        assertFinalFileIs("public " + testFileContent);
    }

    @Test
    public void shouldPreserveNewlinesCrLf() throws IOException {

        final String testFileContent =
            "class ShouldPreserveNewlines {\r\n"
            + "    public static void main(String[] args) {\r\n"
            + "        System.out.println(\"Test\");\r\n"
            + "    }\r\n"
            + "}\r\n"
            + "// note: multiple empty lines at the end\r\n"
            + "\r\n"
            + "\r\n";

        writeContentToTemporaryFile(testFileContent);

        try (MutableDocument documentFile = MutableDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(0, "public ");
        }

        assertFinalFileIs("public " + testFileContent);
    }

    @Test
    public void insertVariousTokensIntoTheFileShouldSucceed() throws IOException {
        writeContentToTemporaryFile("static void main(String[] args) {}");

        try (MutableTextDocument documentFile = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(1, 1, "public ");
            documentFile.insert(17, "final ");
        }

        assertFinalFileIs("public static void main(final String[] args) {}");
    }

    @Test
    public void insertAtTheEndOfTheFileShouldSucceed() throws IOException {
        final String code = "public static void main(String[] args)";
        writeContentToTemporaryFile(code);

        try (MutableTextDocument documentFile = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            documentFile.insert(code.length(), "{}");
        }

        assertFinalFileIs("public static void main(String[] args){}");
    }

    @Test
    public void removeTokenShouldSucceed() throws IOException {
        final String code = "public static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableTextDocument doc = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            doc.delete(doc.createRegion(1, 25, 1, 31));
        }

        assertFinalFileIs("public static void main(String[] args) {}");
    }

    @Test
    public void insertAndRemoveTokensShouldSucceed() throws IOException {
        final String code = "static void main(final String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableTextDocument doc = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            doc.insert(1, 1, "public ");
            doc.delete(doc.createRegion("static void main(".length(), "final ".length()));
        }

        assertFinalFileIs("public static void main(String[] args) {}");
    }

    @Test
    public void insertAndDeleteVariousTokensShouldSucceed() throws IOException {
        final String code = "void main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableTextDocument doc = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            doc.insert(0, "public ");
            doc.insert(0, "static ");
            // delete "void"
            doc.delete(doc.createRegion(0, 4));
            doc.insert(10, "final ");
            // delete "{}"
            doc.delete(doc.createRegion("void main(String[] args) ".length(), 2));
        }

        assertFinalFileIs("public static  main(final String[] args) ");
    }

    @Test
    public void replaceATokenShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableTextDocument doc = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            doc.replace(doc.createRegion(0, 3), "void");
        }

        assertFinalFileIs("void main(String[] args) {}");
    }

    @Test
    public void replaceVariousTokensShouldSucceed() throws IOException {
        final String code = "int main(String[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableTextDocument doc = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            doc.replace(doc.createRegion(1, 1, 1, 1 + "int".length()), "void");
            doc.replace(doc.createRegion(1, 1 + "int ".length(), 1, 1 + "int main".length()), "foo");
            doc.replace(doc.createRegion("int main(".length(), "String".length()), "CharSequence");
        }

        assertFinalFileIs("void foo(CharSequence[] args) {}");
    }

    @Test
    public void insertDeleteAndReplaceVariousTokensShouldSucceed() throws IOException {
        final String code = "static int main(CharSequence[] args) {}";
        writeContentToTemporaryFile(code);

        try (MutableTextDocument doc = MutableTextDocument.forFile(temporaryFile, StandardCharsets.UTF_8)) {
            doc.insert(1, 1, "public");
            // delete "static "
            doc.delete(doc.createRegion(1, 1, 1, 7));
            // replace "int"
            doc.replace(doc.createRegion(1, 8, 1, 8 + "int".length()), "void");
            doc.insert(1, 17, "final ");
            doc.replace(doc.createRegion(1, 17, 1, 17 + "CharSequence".length()), "String");
        }

        assertFinalFileIs("public void main(final String[] args) {}");
    }

    private void assertFinalFileIs(String s) throws IOException {
        final String actualContent = new String(Files.readAllBytes(temporaryFile));
        assertEquals(s, actualContent);
    }

    private void writeContentToTemporaryFile(final String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

}
