/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *  Parsing helper for Kotlin tests.
 */
public class KotlinParsingHelper extends BaseParsingHelper<KotlinParsingHelper, KotlinParser.KtKotlinFile> {

    public static final KotlinParsingHelper DEFAULT = new KotlinParsingHelper(Params.getDefault());

    private static final int BUFFER_BYTES = 8 * 1024;

    private static final int MAX_STD_ERR_LOG_LINES = 5;
    private static final int MAX_STD_ERR_LOG_LINE_LENGTH = 100;

    public KotlinParsingHelper(@NotNull Params params) {
        super(KotlinLanguageModule.getInstance(), KotlinParser.KtKotlinFile.class, params);
    }

    private static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_BYTES];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    public static String readResourcePath(String path) {
        try (InputStream in = KotlinParsingHelper.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Test resource not found on classpath: " + path);
            }
            return new String(readAllBytes(in), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String truncateLogLine(String line) {
        if (line == null) {
            return "";
        }
        if (line.length() <= MAX_STD_ERR_LOG_LINE_LENGTH) {
            return line;
        }
        return line.substring(0, MAX_STD_ERR_LOG_LINE_LENGTH - 3) + "...";
    }

    private static String summarizeStderr(String stderr) {
        if (stderr == null || stderr.trim().isEmpty()) {
            return "";
        }

        String normalized = stderr.replace("\r\n", "\n").replace('\r', '\n').trim();
        String[] lines = normalized.split("\n");

        int maxLines = MAX_STD_ERR_LOG_LINES;
        String header = "stderr had " + lines.length + " line(s), " + normalized.length()
            + " char(s). Showing first " + Math.min(maxLines, lines.length) + " line(s):";

        StringBuilder sb = new StringBuilder();
        sb.append(header).append('\n');

        for (int i = 0; i < lines.length && i < maxLines; i++) {
            sb.append(truncateLogLine(lines[i])).append('\n');
        }
        return sb.toString();
    }

    /**
     * Parses the given code and asserts that there are no parse errors logged to stderr.
     * If there are parse errors, the test will fail with a message summarizing the stderr output.
     */
    static KotlinParser.KtKotlinFile parseAndAssertNoStderr(String code) {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errBuffer));

        try {
            KotlinParser.KtKotlinFile root = DEFAULT.parse(code);
            assertNotNull(root);

            String stderr = new String(errBuffer.toByteArray(), StandardCharsets.UTF_8);
            String normalized = stderr.trim();
            assertEquals("", normalized, "Expected no parse errors on stderr, but " + summarizeStderr(stderr));

            return root;
        } finally {
            System.setErr(originalErr);
        }
    }

    @NotNull
    @Override
    protected KotlinParsingHelper clone(@NotNull Params params) {
        return new KotlinParsingHelper(params);
    }
}
