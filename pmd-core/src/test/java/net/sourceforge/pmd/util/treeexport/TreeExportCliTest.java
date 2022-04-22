/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import static org.hamcrest.Matchers.containsString;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 */
public class TreeExportCliTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testReadStandardInput() {
        IoSpy spy = IoSpy.withStdin("(a(b))");
        int status = spy.runMain("-i", "-f", "xml", "-PlineSeparator=LF");
        Assert.assertEquals(0, status);
        spy.assertThatStdout(containsString("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                            + "<dummyRootNode Image=''>\n"
                                            + "    <dummyNode Image='a'>\n"
                                            + "        <dummyNode Image='b' />\n"
                                            + "    </dummyNode>\n"
                                            + "</dummyRootNode>"));
    }

    @Test
    public void testReadFile() throws IOException {
        File file = newFileWithContents("(a(b))");
        IoSpy spy = new IoSpy();
        int status = spy.runMain("--file", file.getAbsolutePath(), "-f", "xml", "-PlineSeparator=LF");
        Assert.assertEquals(0, status);
        spy.assertThatStdout(containsString("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                            + "<dummyRootNode Image=''>\n"
                                            + "    <dummyNode Image='a'>\n"
                                            + "        <dummyNode Image='b' />\n"
                                            + "    </dummyNode>\n"
                                            + "</dummyRootNode>"));
    }

    private File newFileWithContents(String data) throws IOException {
        File file = tmp.newFile();
        try (BufferedWriter br = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            br.write(data);
        }
        return file;
    }

    private static InputStream stdinContaining(String input) {
        return IOUtils.toInputStream(input, StandardCharsets.UTF_8);
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
            MatcherAssert.assertThat("stdout", out.toString(), str);
        }

        int runMain(String... args) {
            return new TreeExportCli(io).runMain(args);
        }

        static IoSpy withStdin(String contents) {
            return new IoSpy(stdinContaining(contents));
        }
    }
}
