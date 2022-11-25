/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class TreeExportCliTest extends BaseCliTest {

    @TempDir
    private Path tmp;

    @Test
    void testReadStandardInput() throws Exception {
        SystemLambda.withTextFromSystemIn("(a(b))").execute(() -> {
            final CliExecutionResult output = runCliSuccessfully("-i", "-f", "xml", "-PlineSeparator=LF");

            output.checkStdOut(equalTo("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                           + "<dummyRootNode Image=''>\n"
                                           + "    <dummyNode Image='a'>\n"
                                           + "        <dummyNode Image='b' />\n"
                                           + "    </dummyNode>\n"
                                           + "</dummyRootNode>\n"));
        });
    }

    @Test
    void testReadFile() throws Exception {
        File file = newFileWithContents("(a(b))");
        final CliExecutionResult result = runCliSuccessfully("--file", file.getAbsolutePath(), "-f", "xml", "-PlineSeparator=LF");
        result.checkStdOut(equalTo("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                       + "<dummyRootNode Image=''>\n"
                                       + "    <dummyNode Image='a'>\n"
                                       + "        <dummyNode Image='b' />\n"
                                       + "    </dummyNode>\n"
                                       + "</dummyRootNode>\n"));
    }

    private File newFileWithContents(String data) throws IOException {
        File file = Files.createTempFile(tmp, "TreeExportCliTest", "data").toFile();
        try (BufferedWriter br = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            br.write(data);
        }
        return file;
    }

    @Override
    protected List<String> cliStandardArgs() {
        final List<String> argList = new ArrayList<>();
        
        // Set program name and set dummy language
        argList.add("ast-dump");
        argList.add("-l");
        argList.add("dummy");
        
        return argList;
    }
}
