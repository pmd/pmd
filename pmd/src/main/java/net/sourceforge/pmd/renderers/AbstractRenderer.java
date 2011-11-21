/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.AbstractPropertySource;
import net.sourceforge.pmd.util.IOUtil;

/**
 * Abstract base class for {@link Renderer} implementations.
 */
public abstract class AbstractRenderer extends AbstractPropertySource implements Renderer {

    protected String name;
    protected String description;
    protected Map<String, String> propertyDefinitions = new LinkedHashMap<String, String>();
    protected Properties properties;
    protected boolean showSuppressedViolations = true;
    protected Writer writer;

    public AbstractRenderer(String name, String description, Properties properties) {
		this.name = name;
		this.description = description;
		this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
	return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getPropertyDefinitions() {
	return propertyDefinitions;
    }

    /**
     * Define a property.
     * @param name The property name.
     * @param description The description of the property.
     */
    protected void defineProperty(String name, String description) {
	propertyDefinitions.put(name, description);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isShowSuppressedViolations() {
	return showSuppressedViolations;
    }

    /**
     * {@inheritDoc}
     */
    public void setShowSuppressedViolations(boolean showSuppressedViolations) {
	this.showSuppressedViolations = showSuppressedViolations;
    }

    /**
     * {@inheritDoc}
     */
    public void setWriter(Writer writer) {
	    this.writer = writer;
    }

    /**
     * {@inheritDoc}
     */
    public Writer getWriter() {
	    return writer;
    }

    public void flush()  {
        try {
    		this.writer.flush();
    	} catch (IOException e) {
    		throw new IllegalStateException(e);
    	} finally {
    		IOUtil.closeQuietly(writer);
    	}
    }
}
