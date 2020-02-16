/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;

import net.sourceforge.pmd.cpd.renderer.CPDRenderer;

/**
 * CPDTask
 *
 * <p>Runs the CPD utility via ant. The ant task looks like this:</p>
 *
 * <pre>
 * &lt;project name="CPDProj" default="main" basedir="."&gt;
 *   &lt;taskdef name="cpd" classname="net.sourceforge.pmd.cpd.CPDTask" /&gt;
 *   &lt;target name="main"&gt;
 *     &lt;cpd encoding="UTF-16LE" language="java" ignoreIdentifiers="true"
 *          ignoreLiterals="true" ignoreAnnotations="true" minimumTokenCount="100"
 *          outputFile="c:\cpdrun.txt"&gt;
 *       &lt;fileset dir="/path/to/my/src"&gt;
 *         &lt;include name="*.java"/&gt;
 *       &lt;/fileset&gt;
 *     &lt;/cpd&gt;
 *   &lt;/target&gt;
 * &lt;/project&gt;
 * </pre>
 *
 * <p>Required: minimumTokenCount, outputFile, and at least one file</p>
 */
public class CPDTask extends Task {

    private static final String TEXT_FORMAT = "text";
    private static final String XML_FORMAT = "xml";
    private static final String CSV_FORMAT = "csv";

    private String format = TEXT_FORMAT;
    private String language = "java";
    private int minimumTokenCount;
    private boolean ignoreLiterals;
    private boolean ignoreIdentifiers;
    private boolean ignoreAnnotations;
    private boolean ignoreUsings;
    private boolean skipLexicalErrors;
    private boolean skipDuplicateFiles;
    private boolean skipBlocks = true;
    private String skipBlocksPattern = Tokenizer.DEFAULT_SKIP_BLOCKS_PATTERN;
    private File outputFile;
    private String encoding = System.getProperty("file.encoding");
    private List<FileSet> filesets = new ArrayList<>();

    @Override
    public void execute() throws BuildException {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(CPDTask.class.getClassLoader());

        try {
            validateFields();

            log("Starting run, minimumTokenCount is " + minimumTokenCount, Project.MSG_INFO);

            log("Tokenizing files", Project.MSG_INFO);
            CPDConfiguration config = new CPDConfiguration();
            config.setMinimumTileSize(minimumTokenCount);
            config.setLanguage(createLanguage());
            config.setEncoding(encoding);
            config.setSkipDuplicates(skipDuplicateFiles);
            config.setSkipLexicalErrors(skipLexicalErrors);

            CPD cpd = new CPD(config);
            tokenizeFiles(cpd);

            log("Starting to analyze code", Project.MSG_INFO);
            long timeTaken = analyzeCode(cpd);
            log("Done analyzing code; that took " + timeTaken + " milliseconds");

            log("Generating report", Project.MSG_INFO);
            report(cpd);
        } catch (IOException ioe) {
            log(ioe.toString(), Project.MSG_ERR);
            throw new BuildException("IOException during task execution", ioe);
        } catch (ReportException re) {
            re.printStackTrace();
            log(re.toString(), Project.MSG_ERR);
            throw new BuildException("ReportException during task execution", re);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    private Language createLanguage() {
        Properties p = new Properties();
        if (ignoreLiterals) {
            p.setProperty(Tokenizer.IGNORE_LITERALS, "true");
        }
        if (ignoreIdentifiers) {
            p.setProperty(Tokenizer.IGNORE_IDENTIFIERS, "true");
        }
        if (ignoreAnnotations) {
            p.setProperty(Tokenizer.IGNORE_ANNOTATIONS, "true");
        }
        if (ignoreUsings) {
            p.setProperty(Tokenizer.IGNORE_USINGS, "true");
        }
        p.setProperty(Tokenizer.OPTION_SKIP_BLOCKS, Boolean.toString(skipBlocks));
        p.setProperty(Tokenizer.OPTION_SKIP_BLOCKS_PATTERN, skipBlocksPattern);
        return LanguageFactory.createLanguage(language, p);
    }

    private void report(CPD cpd) throws ReportException {
        if (!cpd.getMatches().hasNext()) {
            log("No duplicates over " + minimumTokenCount + " tokens found", Project.MSG_INFO);
        }
        CPDRenderer renderer = createRenderer();

        try {
            // will be closed via BufferedWriter/OutputStreamWriter chain down below
            final OutputStream os;
            if (outputFile == null) {
                os = System.out;
            } else if (outputFile.isAbsolute()) {
                os = Files.newOutputStream(outputFile.toPath());
            } else {
                os = Files.newOutputStream(new File(getProject().getBaseDir(), outputFile.toString()).toPath());
            }

            if (encoding == null) {
                encoding = System.getProperty("file.encoding");
            }

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(os, encoding))) {
                renderer.render(cpd.getMatches(), writer);
            }
        } catch (IOException ioe) {
            throw new ReportException(ioe);
        }
    }

    private void tokenizeFiles(CPD cpd) throws IOException {
        for (FileSet fileSet : filesets) {
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
            String[] includedFiles = directoryScanner.getIncludedFiles();
            for (int i = 0; i < includedFiles.length; i++) {
                File file = new File(
                        directoryScanner.getBasedir() + System.getProperty("file.separator") + includedFiles[i]);
                log("Tokenizing " + file.getAbsolutePath(), Project.MSG_VERBOSE);
                cpd.add(file);
            }
        }
    }

    private long analyzeCode(CPD cpd) {
        long start = System.currentTimeMillis();
        cpd.go();
        long stop = System.currentTimeMillis();
        return stop - start;
    }

    private CPDRenderer createRenderer() {
        if (format.equals(TEXT_FORMAT)) {
            return new SimpleRenderer();
        } else if (format.equals(CSV_FORMAT)) {
            return new CSVRenderer();
        }
        return new XMLRenderer();
    }

    private void validateFields() throws BuildException {
        if (minimumTokenCount == 0) {
            throw new BuildException("minimumTokenCount is required and must be greater than zero");
        }

        if (filesets.isEmpty()) {
            throw new BuildException("Must include at least one FileSet");
        }

        if (!Arrays.asList(LanguageFactory.supportedLanguages).contains(language)) {
            throw new BuildException("Language " + language + " is not supported. Available languages: "
                    + Arrays.toString(LanguageFactory.supportedLanguages));
        }
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    public void setMinimumTokenCount(int minimumTokenCount) {
        this.minimumTokenCount = minimumTokenCount;
    }

    public void setIgnoreLiterals(boolean value) {
        this.ignoreLiterals = value;
    }

    public void setIgnoreIdentifiers(boolean value) {
        this.ignoreIdentifiers = value;
    }

    public void setIgnoreAnnotations(boolean value) {
        this.ignoreAnnotations = value;
    }

    public void setIgnoreUsings(boolean value) {
        this.ignoreUsings = value;
    }

    public void setSkipLexicalErrors(boolean skipLexicalErrors) {
        this.skipLexicalErrors = skipLexicalErrors;
    }

    public void setSkipDuplicateFiles(boolean skipDuplicateFiles) {
        this.skipDuplicateFiles = skipDuplicateFiles;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void setFormat(FormatAttribute formatAttribute) {
        this.format = formatAttribute.getValue();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setSkipBlocks(boolean skipBlocks) {
        this.skipBlocks = skipBlocks;
    }

    public void setSkipBlocksPattern(String skipBlocksPattern) {
        this.skipBlocksPattern = skipBlocksPattern;
    }

    public static class FormatAttribute extends EnumeratedAttribute {
        private static final String[] FORMATS = new String[] { XML_FORMAT, TEXT_FORMAT, CSV_FORMAT };

        @Override
        public String[] getValues() {
            return FORMATS;
        }
    }
}
