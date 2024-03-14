/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * <p>
 * A console renderer with color support for terminal supporting ansi color codes.
 * </p>
 *
 * <pre>
 * * file: ./src/gilot/Test.java
 *     src:  Test.java:12
 *     rule: AtLeastOneConstructor
 *     msg:  Each class should declare at least one constructor
 *     code: public class Test
 *
 * * file: ./src/gilot/log/format/LogInterpreter.java
 *     src:  LogInterpreter.java:317
 *     rule: AvoidDuplicateLiterals
 *     msg:  The same String literal appears 4 times in this file; the first occurrence is on line 317
 *     code: logger.error( "missing attribute 'app_arg' in rule '" + ((Element)element.getParent()).getAttributeValue( "name" ) + "'" );
 *
 *     src:  LogInterpreter.java:317
 *     rule: AvoidDuplicateLiterals
 *     msg:  The same String literal appears 5 times in this file; the first occurrence is on line 317
 *     code: logger.error( "missing attribute 'app_arg' in rule '" + ((Element)element.getParent()).getAttributeValue( "name" ) + "'" );
 * * warnings: 3
 * </pre>
 */
public class TextColorRenderer extends AbstractAccumulatingRenderer {

    public static final String NAME = "textcolor";

    /**
     * Directory from where java was invoked.
     */
    private String pwd;

    private String yellowBold = "\u001B[1;33m";
    private String whiteBold = "\u001B[1;37m";
    private String redBold = "\u001B[1;31m";
    private String red = "\u001B[0;31m";
    private String green = "\u001B[0;32m";
    private String cyan = "\u001B[0;36m";

    private String colorReset = "\u001B[0m";

    public TextColorRenderer() {
        // This Renderer was originally submitted by Adrian Papari and was
        // called the "PapariTextRenderer" pre-PMD 5.0.
        super(NAME, "Text format, with color support (requires ANSI console support, e.g. xterm, rxvt, etc.).");
    }

    @Override
    public String defaultFileExtension() {
        return "txt";
    }

    @Override
    public void outputReport(Report report) throws IOException {
        StringBuilder buf = new StringBuilder(500);
        buf.append(System.lineSeparator());
        String lastFile = null;
        int numberOfErrors = 0;
        int numberOfWarnings = 0;

        for (RuleViolation rv : report.getViolations()) {
            buf.setLength(0);
            numberOfWarnings++;
            String nextFile = determineFileName(rv.getFileId());
            if (!nextFile.equals(lastFile)) {
                lastFile = nextFile;
                buf.append(this.yellowBold)
                        .append("*")
                        .append(this.colorReset)
                        .append(" file: ")
                        .append(this.whiteBold)
                        .append(this.getRelativePath(lastFile))
                        .append(this.colorReset)
                        .append(System.lineSeparator());
            }
            buf.append(this.green)
                    .append("    src:  ")
                    .append(this.cyan)
                    .append(lastFile.substring(lastFile.lastIndexOf(File.separator) + 1))
                    .append(this.colorReset).append(":")
                    .append(this.cyan)
                    .append(rv.getBeginLine())
                    .append(rv.getEndLine() == -1 ? "" : ":" + rv.getEndLine())
                    .append(this.colorReset)
                    .append(System.lineSeparator());
            buf.append(this.green)
                    .append("    rule: ")
                    .append(this.colorReset)
                    .append(rv.getRule().getName())
                    .append(System.lineSeparator());
            buf.append(this.green)
                    .append("    msg:  ")
                    .append(this.colorReset)
                    .append(rv.getDescription())
                    .append(System.lineSeparator());
            buf.append(this.green)
                    .append("    code: ")
                    .append(this.colorReset)
                    .append(this.getLine(lastFile, rv.getBeginLine()))
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            writer.write(buf.toString());
        }
        writer.println();
        writer.println();
        writer.println("Summary:");
        writer.println();
        for (Map.Entry<String, Integer> entry : getCountSummary(report).entrySet()) {
            buf.setLength(0);
            String key = entry.getKey();
            buf.append(key).append(" : ").append(entry.getValue());
            writer.println(buf);
        }

        for (ProcessingError error : report.getProcessingErrors()) {
            buf.setLength(0);
            numberOfErrors++;
            String nextFile = determineFileName(error.getFileId());
            if (!nextFile.equals(lastFile)) {
                lastFile = nextFile;
                buf.append(this.redBold)
                        .append("*")
                        .append(this.colorReset)
                        .append(" file: ")
                        .append(this.whiteBold)
                        .append(this.getRelativePath(lastFile))
                        .append(this.colorReset)
                        .append(System.lineSeparator());
            }
            buf.append(this.green)
                    .append("    err:  ")
                    .append(this.cyan)
                    .append(error.getMsg())
                    .append(this.colorReset)
                    .append(System.lineSeparator())
                    .append(this.red)
                    .append(error.getDetail())
                    .append(colorReset)
                    .append(System.lineSeparator());
            writer.println(buf);
        }

        for (ConfigurationError error : report.getConfigurationErrors()) {
            buf.setLength(0);
            numberOfErrors++;
            buf.append(this.redBold)
                    .append("*")
                    .append(this.colorReset)
                    .append(" rule: ")
                    .append(this.whiteBold)
                    .append(error.rule().getName())
                    .append(this.colorReset)
                    .append(System.lineSeparator());
            buf.append(this.green)
                    .append("    err:  ")
                    .append(this.cyan)
                    .append(error.issue())
                    .append(this.colorReset)
                    .append(System.lineSeparator());
            writer.println(buf);
        }

        // adding error message count, if any
        if (numberOfErrors > 0) {
            writer.println(this.redBold + "*" + this.colorReset + " errors:   " + this.whiteBold + numberOfErrors
                    + this.colorReset);
        }
        writer.println(this.yellowBold + "*" + this.colorReset + " warnings: " + this.whiteBold + numberOfWarnings
                + this.colorReset);
    }


    /**
     * Calculate a summary of violation counts per fully classified class name.
     *
     * @return violations per class name
     */
    private static Map<String, Integer> getCountSummary(Report report) {
        Map<String, Integer> summary = new HashMap<>();
        for (RuleViolation rv : report.getViolations()) {
            String key = keyFor(rv);
            if (key.isEmpty()) {
                continue;
            }
            Integer o = summary.get(key);
            summary.put(key, o == null ? 1 : o + 1);
        }
        return summary;
    }

    private static String keyFor(RuleViolation rv) {
        String packageName = rv.getAdditionalInfo().getOrDefault(RuleViolation.PACKAGE_NAME, "");
        String className = rv.getAdditionalInfo().getOrDefault(RuleViolation.CLASS_NAME, "");
        return StringUtils.isNotBlank(packageName) ? packageName + '.' + className : "";
    }


    /**
     * Retrieves the requested line from the specified file.
     *
     * @param sourceFile
     *            the java or cpp source file
     * @param line
     *            line number to extract
     *
     * @return a trimmed line of source code
     */
    private String getLine(String sourceFile, int line) {
        String code = null;
        try (BufferedReader br = new BufferedReader(getReader(sourceFile))) {
            for (int i = 0; line > i; i++) {
                String txt = br.readLine();
                code = txt == null ? "" : txt.trim();
            }
        } catch (IOException ioErr) {
            ioErr.printStackTrace();
        }
        return code;
    }

    protected Reader getReader(String sourceFile) throws FileNotFoundException {
        try {
            return Files.newBufferedReader(new File(sourceFile).toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            FileNotFoundException ex = new FileNotFoundException(sourceFile);
            ex.initCause(e);
            throw ex;
        }
    }

    /**
     * Attempts to determine the relative path to the file. If relative path
     * cannot be found, the original path is returnedi, ie - the current path
     * for the supplied file.
     *
     * @param fileName
     *            well, the file with its original path.
     * @return the relative path to the file
     */
    private String getRelativePath(String fileName) {
        String relativePath;

        // check if working directory need to be assigned
        if (pwd == null) {
            try {
                this.pwd = new File(".").getCanonicalPath();
            } catch (IOException ioErr) {
                // to avoid further error
                this.pwd = "";
            }
        }

        // make sure that strings match before doing any substring-ing
        if (fileName.indexOf(this.pwd) == 0) {
            relativePath = "." + fileName.substring(this.pwd.length());

            // remove current dir occurring twice - occurs if . was supplied as
            // path
            if (relativePath.startsWith("." + File.separator + "." + File.separator)) {
                relativePath = relativePath.substring(2);
            }
        } else {
            // this happens when pmd's supplied argument deviates from the pwd
            // 'branch' (god knows this terminology - i hope i make some sense).
            // for instance, if supplied=/usr/lots/of/src and
            // pwd=/usr/lots/of/shared/source
            // TODO: a fix to get relative path?
            relativePath = fileName;
        }

        return relativePath;
    }
}
