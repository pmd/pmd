/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.properties.AbstractPropertySource;

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
}
