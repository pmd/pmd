/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class RawFileFingerprinterTest extends AbstractClasspathEntryFingerprinterTest {

    @Override
    protected ClasspathEntryFingerprinter newFingerPrinter() {
        return new RawFileFingerprinter();
    }

    @Override
    protected String[] getValidFileExtensions() {
        return new String[] { "class" };
    }

    @Override
    protected String[] getInvalidFileExtensions() {
        return new String[] { "xml" };
    }

    @Override
    protected File createValidNonEmptyFile() throws IOException {
        Path file = tempDir.resolve("Foo.class");
        Files.write(file, "some content".getBytes(StandardCharsets.UTF_8));
        return file.toFile();
    }
}
