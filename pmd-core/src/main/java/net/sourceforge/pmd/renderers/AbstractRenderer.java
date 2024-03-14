/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Objects;

import net.sourceforge.pmd.PMDConfiguration;
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
    private FileNameRenderer fileNameRenderer = fileId -> fileId.getOriginalPath();

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
        this.fileNameRenderer = Objects.requireNonNull(fileNameRenderer);
    }

    /**
     * Determines the filename that should be used in the report for the
     * given ID. This uses the {@link FileNameRenderer} of this renderer.
     * In the PMD CLI, the file name renderer respects the {@link PMDConfiguration#getRelativizeRoots()}
     * relativize roots to output relative paths.
     *
     * <p>A renderer does not have to use this method to output paths.
     * Some report formats require a specific format for paths, eg URIs.
     * They can implement this ad-hoc.
     */
    protected final String determineFileName(FileId fileId) {
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
    // TODO: consider to rename the flush method - this is actually closing the writer
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
    @Override
    public void setReportFile(String reportFilename) {
        this.setWriter(IOUtil.createWriter(reportFilename));
    }
}
