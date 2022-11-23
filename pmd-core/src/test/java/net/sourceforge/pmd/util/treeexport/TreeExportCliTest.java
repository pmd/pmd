/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 */
class TreeExportCliTest {

    @TempDir
    private Path tmp;

    @Test
    void testReadStandardInput() {
        IoSpy spy = IoSpy.withStdin("(a(b))");
        int status = spy.runMain("-i", "-f", "xml", "-PlineSeparator=LF", "-l", "dummy");
        assertEquals(0, status);
        spy.assertThatStdout(containsString("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                            + "<dummyRootNode Image=''>\n"
                                            + "    <dummyNode Image='a'>\n"
                                            + "        <dummyNode Image='b' />\n"
                                            + "    </dummyNode>\n"
                                            + "</dummyRootNode>"));
    }

    @Test
    void testReadFile() throws IOException {
        File file = newFileWithContents("(a(b))");
        IoSpy spy = new IoSpy();
        int status = spy.runMain("--file", file.getAbsolutePath(), "-f", "xml", "-PlineSeparator=LF", "-l", "dummy");
        assertEquals(0, status);
        spy.assertThatStdout(containsString("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                            + "<dummyRootNode Image=''>\n"
                                            + "    <dummyNode Image='a'>\n"
                                            + "        <dummyNode Image='b' />\n"
                                            + "    </dummyNode>\n"
                                            + "</dummyRootNode>"));
    }

    private File newFileWithContents(String data) throws IOException {
        File file = Files.createTempFile(tmp, "TreeExportCliTest", "data").toFile();
        try (BufferedWriter br = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            br.write(data);
        }
        return file;
    }

    private static InputStream stdinContaining(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    static class IoSpy {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final Io io;

        IoSpy(InputStream stdin) {
            io = new Io(new PrintStream(out), new PrintStream(err), stdin);
        }

        IoSpy() {
            this(stdinContaining(""));
        }

        void assertThatStdout(Matcher<? super String> str) {
            assertThat("stdout", out.toString(), str);
        }

        int runMain(String... args) {
            return new TreeExportCli(io).runMain(args);
        }

        static IoSpy withStdin(String contents) {
            return new IoSpy(stdinContaining(contents));
        }
    }
}
