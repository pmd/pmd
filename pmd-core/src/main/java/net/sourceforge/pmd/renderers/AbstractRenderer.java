/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.PrintWriter;
import java.io.Writer;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.reporting.FileNameRenderer;

/**
 * Abstract base class for {@link Renderer} implementations.
 */
public abstract class AbstractRenderer extends AbstractPropertySource implements Renderer {
    protected String name;
    protected String description;

    protected boolean showSuppressedViolations = true;
    protected PrintWriter writer;
    private FileNameRenderer fileNameRenderer = FileId::getOriginalPath;

    public AbstractRenderer(String name, String description) {
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
    public void setFileNameRenderer(FileNameRenderer fileNameRenderer) {
        this.fileNameRenderer = fileNameRenderer;
    }

    /**
     * Determines the filename that should be used in the report depending on the
     * option "shortnames". If the option is enabled, then the filename in the report
     * is without the directory prefix of the directories, that have been analyzed.
     * If the option "shortnames" is not enabled, then the fileId is returned as-is.
     */
    protected String determineFileName(FileId fileId) {
        return fileNameRenderer.getDisplayName(fileId);
    }

    @Override
    public void setWriter(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    @Override
    public Writer getWriter() {
        return writer;
    }

    @Override
    public void flush() {
        if (writer == null) {
            // might happen, if no writer is set. E.g. in maven-pmd-plugin's PmdCollectingRenderer
            return;
        }

        try {
            this.writer.flush();
        } finally {
            IOUtil.closeQuietly(writer);
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
}
