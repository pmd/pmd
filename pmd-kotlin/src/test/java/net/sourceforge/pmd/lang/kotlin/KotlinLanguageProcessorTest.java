/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class KotlinLanguageProcessorTest {

    // --- sanitizeKtFilename ---

    @Test
    void sanitizeKtFilenameKeepsKtExtension() {
        assertEquals("Foo.kt", KotlinLanguageProcessor.sanitizeKtFilename("/path/to/Foo.kt"));
    }

    @Test
    void sanitizeKtFilenameAppendsKtToExtensionlessName() {
        assertEquals("Foo.kt", KotlinLanguageProcessor.sanitizeKtFilename("/path/to/Foo"));
    }

    @Test
    void sanitizeKtFilenameAppendsKtToOtherExtension() {
        assertEquals("Foo.java.kt", KotlinLanguageProcessor.sanitizeKtFilename("/path/to/Foo.java"));
    }

    @Test
    void sanitizeKtFilenameHandlesEmptyPath() {
        assertEquals("snippet.kt", KotlinLanguageProcessor.sanitizeKtFilename(""));
    }

    @Test
    void sanitizeKtFilenameHandlesRootPath() {
        assertEquals("snippet.kt", KotlinLanguageProcessor.sanitizeKtFilename("/"));
    }

    @Test
    void sanitizeKtFilenameHandlesJustFilename() {
        assertEquals("MyFile.kt", KotlinLanguageProcessor.sanitizeKtFilename("MyFile"));
    }

    @Test
    void sanitizeKtFilenameHandlesJustFilenameWithKt() {
        assertEquals("MyFile.kt", KotlinLanguageProcessor.sanitizeKtFilename("MyFile.kt"));
    }
}
