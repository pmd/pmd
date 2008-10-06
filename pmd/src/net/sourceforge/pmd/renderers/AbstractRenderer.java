/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Abstract base class for {@link Renderer} implementations.
 */
public abstract class AbstractRenderer implements Renderer {

    protected String name;
    protected String description;
    protected Map<String, String> propertyDefinitions = new LinkedHashMap<String, String>();
    protected Properties properties;
    protected boolean showSuppressedViolations = true;
    private Writer writer;
    private Report mainReport;

    public AbstractRenderer(String name, String description, java.util.Properties properties) {
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
    public String render(Report report) {
	StringWriter w = new StringWriter();
	try {
	    render(w, report);
	} catch (IOException e) {
	    throw new Error("StringWriter doesn't throw IOException", e);
	}
	return w.toString();
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

    /**
     * {@inheritDoc}
     */
    public void start() throws IOException {
	// default (and backward compatible) behavior is to build a full report.
	// Optimized rendering is done in AbstractIncrementalRenderer and descendants
	mainReport = new Report();
    }

    /**
     * {@inheritDoc}
     */
    public void startFileAnalysis(DataSource dataSource) {
    }

    /**
     * {@inheritDoc}
     */
    public void renderFileReport(Report report) throws IOException {
	// default (and backward compatible) behavior is to build a full report.
	// Optimized rendering is done in AbstractIncrementalRenderer and descendants
	mainReport.merge(report);
    }

    /**
     * {@inheritDoc}
     */
    public void end() throws IOException {
	// default (and backward compatible) behavior is to build a full report.
	// Optimized rendering is done in AbstractIncrementalRenderer and descendants
	render(writer, mainReport);
    }
}
