/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCode;
import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.cli.PMDParameters;
import net.sourceforge.pmd.internal.util.ShortFilenameUtil;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.IOUtil;

/**
 * Abstract base class for {@link Renderer} implementations.
 */
public abstract class AbstractRenderer extends AbstractPropertySource implements Renderer {
    protected String name;
    protected String description;

    protected boolean showSuppressedViolations = true;
    protected Writer writer;

    protected List<String> inputPathPrefixes = Collections.emptyList();

    private static final PropertyDescriptor<String> SOURCE_ENCODING_PROPERTY =
            PropertyFactory.stringProperty("sourceEncoding")
                    .defaultValue("UTF-8")
                    .desc("Source code encoding")
                    .build();

    public AbstractRenderer(String name, String description) {
        definePropertyDescriptor(SOURCE_ENCODING_PROPERTY);

        this.name = name;
        this.description = description;
    }

    @Override
    protected String getPropertySourceType() {
        return "renderer";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isShowSuppressedViolations() {
        return showSuppressedViolations;
    }

    @Override
    public void setShowSuppressedViolations(boolean showSuppressedViolations) {
        this.showSuppressedViolations = showSuppressedViolations;
    }

    @Override
    public void setUseShortNames(List<String> inputPaths) {
        this.inputPathPrefixes = inputPaths;
    }

    /**
     * Determines the filename that should be used in the report depending on the
     * option "shortnames". If the option is enabled, then the filename in the report
     * is without the directory prefix of the directories, that have been analyzed.
     * If the option "shortnames" is not enabled, then the inputFileName is returned as-is.
     *
     * @param inputFileName
     * @return
     * @see PMDConfiguration#isReportShortNames()
     * @see PMDParameters#isShortnames()
     */
    protected String determineFileName(String inputFileName) {
        return ShortFilenameUtil.determineFileName(inputPathPrefixes, inputFileName);
    }

    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Writer getWriter() {
        return writer;
    }

    @Override
    public void flush() {
        try {
            this.writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>This default implementation always uses the system default charset for the writer.
     * Overwrite in specific renderers to support other charsets.
     */
    @Experimental
    @Override
    public void setReportFile(String reportFilename) {
        this.setWriter(IOUtil.createWriter(reportFilename));
    }

    @Experimental
    @Override
    public String getSourceEncoding() {
        return getProperty(SOURCE_ENCODING_PROPERTY);
    }

    @Experimental
    @Override
    public void setSourceEncoding(String sourceEncoding) {
        setProperty(SOURCE_ENCODING_PROPERTY, sourceEncoding);
    }

    @Experimental
    @Override
    public SourceCode getSourceCode(RuleViolation violation) {
        if ("".equals(violation.getFilename())
                || Files.notExists(Paths.get(violation.getFilename()))) {
            return null;
        }

        return new SourceCode(new SourceCode.FileCodeLoader(
                new File(violation.getFilename()), getSourceEncoding()));

    }

}
