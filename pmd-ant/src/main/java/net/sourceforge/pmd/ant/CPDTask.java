/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;

import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CPDReport;
import net.sourceforge.pmd.cpd.CPDReportRenderer;
import net.sourceforge.pmd.cpd.CSVRenderer;
import net.sourceforge.pmd.cpd.CpdAnalysis;
import net.sourceforge.pmd.cpd.SimpleRenderer;
import net.sourceforge.pmd.cpd.XMLOldRenderer;
import net.sourceforge.pmd.cpd.XMLRenderer;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

/**
 * CPD Ant task. Setters of this class are interpreted by Ant as properties
 * settable in the XML. This is therefore published API.
 *
 * <p>Runs the CPD utility via ant. The ant task looks like this:</p>
 *
 * <pre>{@code
 *   <project name="CPDProject" default="main" basedir=".">
 *     <path id="pmd.classpath">
 *         <fileset dir="/home/joe/pmd-bin-VERSION/lib">
 *             <include name="*.jar"/>
 *         </fileset>
 *     </path>
 *     <taskdef name="cpd" classname="net.sourceforge.pmd.ant.CPDTask" classpathref="pmd.classpath" />
 *
 *     <target name="main">
 *       <cpd encoding="UTF-16LE" language="java" ignoreIdentifiers="true"
 *            ignoreLiterals="true" ignoreAnnotations="true" minimumTokenCount="100"
 *            outputFile="c:\cpdrun.txt">
 *         <fileset dir="/path/to/my/src">
 *           <include name="*.java"/>
 *         </fileset>
 *       </cpd>
 *     </target>
 *   </project>
 * }</pre>
 *
 * <p>Required: minimumTokenCount, outputFile, and at least one file</p>
 */
public class CPDTask extends Task {

    private static final String TEXT_FORMAT = "text";
    private static final String XML_FORMAT = "xml";
    @Deprecated
    private static final String XMLOLD_FORMAT = "xmlold";
    private static final String CSV_FORMAT = "csv";

    private String format = TEXT_FORMAT;
    private String language = "java";
    private int minimumTokenCount;
    private boolean ignoreLiterals;
    private boolean ignoreIdentifiers;
    private boolean ignoreAnnotations;
    private boolean ignoreUsings;
    @Deprecated
    private boolean skipLexicalErrors;
    private boolean skipDuplicateFiles;
    private boolean skipBlocks = true;
    private String skipBlocksPattern;
    private File outputFile;
    private String encoding = System.getProperty("file.encoding");
    private List<FileSet> filesets = new ArrayList<>();
    private boolean failOnError = true;

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
            config.setOnlyRecognizeLanguage(config.getLanguageRegistry().getLanguageById(language));
            config.setSourceEncoding(Charset.forName(encoding));
            config.setSkipDuplicates(skipDuplicateFiles);

            if (skipLexicalErrors) {
                log("skipLexicalErrors is deprecated since 7.3.0 and the property is ignored. "
                        + "Lexical errors are now skipped by default and the build is failed. "
                        + "Use failOnError=\"false\" to not fail the build.", Project.MSG_WARN);
            }

            config.setIgnoreAnnotations(ignoreAnnotations);
            config.setIgnoreLiterals(ignoreLiterals);
            config.setIgnoreIdentifiers(ignoreIdentifiers);
            config.setIgnoreUsings(ignoreUsings);
            if (skipBlocks) {
                config.setSkipBlocksPattern(skipBlocksPattern);
            }

            try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
                addFiles(cpd);

                log("Starting to analyze code", Project.MSG_INFO);
                long start = System.currentTimeMillis();
                cpd.performAnalysis(this::report);
                long timeTaken = System.currentTimeMillis() - start;
                log("Done analyzing code; that took " + timeTaken + " milliseconds");

                int errors = config.getReporter().numErrors();
                if (errors > 0) {
                    String message = String.format("There were %d recovered errors during analysis.", errors);
                    if (failOnError) {
                        throw new BuildException(message + " Ignore these with failOnError=\"true\".");
                    } else {
                        log(message + " Not failing build, because failOnError=\"false\".", Project.MSG_WARN);
                    }
                }
            }
        } catch (IOException ioe) {
            log(ioe.toString(), Project.MSG_ERR);
            throw new BuildException("IOException during task execution", ioe);
        } catch (ReportException re) {
            log(re.toString(), Project.MSG_ERR);
            throw new BuildException("ReportException during task execution", re);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    private void report(CPDReport report) throws ReportException {
        if (report.getMatches().isEmpty()) {
            log("No duplicates over " + minimumTokenCount + " tokens found", Project.MSG_INFO);
        }
        log("Generating report", Project.MSG_INFO);
        CPDReportRenderer renderer = createRenderer();

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
                renderer.render(report, writer);
            }
        } catch (IOException ioe) {
            throw new ReportException(ioe);
        }
    }

    private void addFiles(CpdAnalysis cpd) throws IOException {
        for (FileSet fileSet : filesets) {
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
            String[] includedFiles = directoryScanner.getIncludedFiles();
            for (String includedFile : includedFiles) {
                Path file = directoryScanner.getBasedir().toPath().resolve(includedFile);
                cpd.files().addFile(file);
            }
        }
    }

    private CPDReportRenderer createRenderer() {
        if (TEXT_FORMAT.equals(format)) {
            return new SimpleRenderer();
        } else if (CSV_FORMAT.equals(format)) {
            return new CSVRenderer();
        } else if (XMLOLD_FORMAT.equals(format)) {
            return new XMLOldRenderer();
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

        if (LanguageRegistry.CPD.getLanguageById(language) == null) {
            throw new BuildException("Language " + language + " is not supported. Available languages: "
                    + LanguageRegistry.CPD.commaSeparatedList(Language::getId));
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

    /**
     * @deprecated Use {@link #setFailOnError(boolean)} instead.
     */
    @Deprecated
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

    /**
     * Whether to fail the build if any recoverable errors occurred while processing the files.
     *
     * @since 7.3.0
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public static class FormatAttribute extends EnumeratedAttribute {
        private static final String[] FORMATS = new String[] { XML_FORMAT, TEXT_FORMAT, CSV_FORMAT, XMLOLD_FORMAT };

        @Override
        public String[] getValues() {
            return FORMATS;
        }
    }
}
