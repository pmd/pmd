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
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * <p>
 * A console renderer with optional color support under *nix systems.
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
 * <p>
 * Colorization is turned on by supplying -D<b>pmd.color</b> - any value other
 * than '0' or 'false', enables color - including an empty value (''). <b>Nota
 * Bene:</b> colorization is atm only supported under *nix terminals accepting
 * ansi escape sequences, such as xterm, rxvt et cetera.
 * </p>
 */
public class TextColorRenderer extends AbstractAccumulatingRenderer {

    public static final String NAME = "textcolor";

    // What? TODO 7.0.0 Use a boolean property
    public static final StringProperty COLOR = new StringProperty("color",
            "Enables colors with anything other than 'false' or '0'.", "yes", 0);
    private static final String SYSTEM_PROPERTY_PMD_COLOR = "pmd.color";

    /**
     * Directory from where java was invoked.
     */
    private String pwd;

    private String yellowBold = "";
    private String whiteBold = "";
    private String redBold = "";
    private String red = "";
    private String cyan = "";
    private String green = "";

    private String colorReset = "";

    public TextColorRenderer() {
        // This Renderer was originally submitted by Adrian Papari and was
        // called the "PapariTextRenderer" pre-PMD 5.0.
        super(NAME, "Text format, with color support (requires ANSI console support, e.g. xterm, rxvt, etc.).");
        definePropertyDescriptor(COLOR);
    }

    @Override
    public String defaultFileExtension() {
        return "txt";
    }

    /**
     * Enables colors on *nix systems - not windows. Color support depends on
     * the pmd.color property, which should be set with the -D option during
     * execution - a set value other than 'false' or '0' enables color.
     * <p/>
     * btw, is it possible to do this on windows (ie; console colors)?
     */
    private void initializeColorsIfSupported() {
        if (isPropertyEnabled(getProperty(COLOR)) || isPropertyEnabled(System.getProperty(SYSTEM_PROPERTY_PMD_COLOR))) {
            this.yellowBold = "\u001B[1;33m";
            this.whiteBold = "\u001B[1;37m";
            this.redBold = "\u001B[1;31m";
            this.red = "\u001B[0;31m";
            this.green = "\u001B[0;32m";
            this.cyan = "\u001B[0;36m";

            this.colorReset = "\u001B[0m";
        }
    }

    private boolean isPropertyEnabled(String property) {
        return property != null && !("0".equals(property) || "false".equalsIgnoreCase(property));
    }

    @Override
    public void end() throws IOException {
        StringBuilder buf = new StringBuilder(500);
        buf.append(PMD.EOL);
        initializeColorsIfSupported();
        String lastFile = null;
        int numberOfErrors = 0;
        int numberOfWarnings = 0;

        for (Iterator<RuleViolation> i = report.iterator(); i.hasNext();) {
            buf.setLength(0);
            numberOfWarnings++;
            RuleViolation rv = i.next();
            String nextFile = determineFileName(rv.getFilename());
            if (!nextFile.equals(lastFile)) {
                lastFile = nextFile;
                buf.append(this.yellowBold + "*" + this.colorReset + " file: " + this.whiteBold
                        + this.getRelativePath(lastFile) + this.colorReset + PMD.EOL);
            }
            buf.append(
                    this.green + "    src:  " + this.cyan + lastFile.substring(lastFile.lastIndexOf(File.separator) + 1)
                            + this.colorReset + ":" + this.cyan + rv.getBeginLine()
                            + (rv.getEndLine() == -1 ? "" : ":" + rv.getEndLine()) + this.colorReset + PMD.EOL);
            buf.append(this.green + "    rule: " + this.colorReset + rv.getRule().getName() + PMD.EOL);
            buf.append(this.green + "    msg:  " + this.colorReset + rv.getDescription() + PMD.EOL);
            buf.append(this.green + "    code: " + this.colorReset + this.getLine(lastFile, rv.getBeginLine()) + PMD.EOL
                    + PMD.EOL);
            writer.write(buf.toString());
        }
        writer.write(PMD.EOL + PMD.EOL);
        writer.write("Summary:" + PMD.EOL + PMD.EOL);
        Map<String, Integer> summary = report.getCountSummary();
        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            buf.setLength(0);
            String key = entry.getKey();
            buf.append(key).append(" : ").append(entry.getValue()).append(PMD.EOL);
            writer.write(buf.toString());
        }

        for (Iterator<Report.ProcessingError> i = report.errors(); i.hasNext();) {
            buf.setLength(0);
            numberOfErrors++;
            Report.ProcessingError error = i.next();
            String nextFile = determineFileName(error.getFile());
            if (!nextFile.equals(lastFile)) {
                lastFile = nextFile;
                buf.append(this.redBold + "*" + this.colorReset + " file: " + this.whiteBold
                        + this.getRelativePath(lastFile) + this.colorReset + PMD.EOL);
            }
            buf.append(this.green + "    err:  " + this.cyan + error.getMsg() + this.colorReset + PMD.EOL)
                .append(this.red).append(error.getDetail()).append(colorReset).append(PMD.EOL).append(PMD.EOL);
            writer.write(buf.toString());
        }

        for (Iterator<Report.ConfigurationError> i = report.configErrors(); i.hasNext();) {
            buf.setLength(0);
            numberOfErrors++;
            Report.ConfigurationError error = i.next();
            buf.append(this.redBold + "*" + this.colorReset + " rule: " + this.whiteBold
                    + error.rule().getName() + this.colorReset + PMD.EOL);
            buf.append(this.green + "    err:  " + this.cyan + error.issue() + this.colorReset + PMD.EOL + PMD.EOL);
            writer.write(buf.toString());
        }

        // adding error message count, if any
        if (numberOfErrors > 0) {
            writer.write(this.redBold + "*" + this.colorReset + " errors:   " + this.whiteBold + numberOfErrors
                    + this.colorReset + PMD.EOL);
        }
        writer.write(this.yellowBold + "*" + this.colorReset + " warnings: " + this.whiteBold + numberOfWarnings
                + this.colorReset + PMD.EOL);
    }

    /**
     * Retrieves the requested line from the specified file.
     *
     * @param sourceFile
     *            the java or cpp source file
     * @param line
     *            line number to extract
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

            // remove current dir occuring twice - occurs if . was supplied as
            // path
            if (relativePath.startsWith("." + File.separator + "." + File.separator)) {
                relativePath = relativePath.substring(2);
            }
        } else {
            // this happens when pmd's supplied argument deviates from the pwd
            // 'branch' (god knows this terminolgy - i hope i make some sense).
            // for instance, if supplied=/usr/lots/of/src and
            // pwd=/usr/lots/of/shared/source
            // TODO: a fix to get relative path?
            relativePath = fileName;
        }

        return relativePath;
    }
}
