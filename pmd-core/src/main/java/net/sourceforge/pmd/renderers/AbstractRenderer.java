/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.AbstractPropertySource;

/**
 * Abstract base class for {@link Renderer} implementations.
 */
public abstract class AbstractRenderer extends AbstractPropertySource implements Renderer {

    protected String name;
    protected String description;

    protected boolean showSuppressedViolations = true;
    protected Writer writer;

    public AbstractRenderer(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShowSuppressedViolations() {
        return showSuppressedViolations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShowSuppressedViolations(boolean showSuppressedViolations) {
        this.showSuppressedViolations = showSuppressedViolations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * {@inheritDoc}
     */
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
}
