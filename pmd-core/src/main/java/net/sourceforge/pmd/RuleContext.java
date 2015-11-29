/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * The RuleContext provides access to Rule processing state.  This information
 * includes the following global information:
 * <ul>
 * 	<li>The Report to which Rule Violations are sent.</li>
 * 	<li>Named attributes.</li>
 * </ul>
 * As well as the following source file specific information:
 * <ul>
 * 	<li>A File for the source file.</li>
 * 	<li>A String for the name of the source file.</li>
 * 	<li>The Language Version of the source file.</li>
 * </ul>
 * It is <strong>required</strong> that all source file specific options
 * be set between calls to difference source files.  Failure to do so, may
 * result in undefined behavior.
 */
public class RuleContext {

    private Report report = new Report();
    private File sourceCodeFile;
    private String sourceCodeFilename;
    private LanguageVersion languageVersion;
    private final Map<String, Object> attributes;
    private boolean ignoreExceptions = true;

    /**
     * Default constructor.
     */
    public RuleContext() {
	attributes = new ConcurrentHashMap<>();
    }

    /**
     * Constructor which shares attributes and report listeners with the given RuleContext.
     * @param ruleContext the context from which the values are shared
     */
    public RuleContext(RuleContext ruleContext) {
	this.attributes = ruleContext.attributes;
	this.report.addSynchronizedListeners(ruleContext.getReport().getSynchronizedListeners());
    }

    /**
     * Get the Report to which Rule Violations are sent.
     * @return The Report.
     */
    public Report getReport() {
	return report;
    }

    /**
     * Set the Report to which Rule Violations are sent.
     * @param report The Report.
     */
    public void setReport(Report report) {
	this.report = report;
    }

    /**
     * Get the File associated with the current source file.
     * @return The File.
     */
    public File getSourceCodeFile() {
	return sourceCodeFile;
    }

    /**
     * Set the File associated with the current source file.
     * While this may be set to <code>null</code>, the exclude/include
     * facilities will not work properly without a File.
     * @param sourceCodeFile The File.
     */
    public void setSourceCodeFile(File sourceCodeFile) {
	this.sourceCodeFile = sourceCodeFile;
    }

    /**
     * Get the file name associated with the current source file.
     * @return The file name.
     */
    public String getSourceCodeFilename() {
	return sourceCodeFilename;
    }

    /**
     * Set the file name associated with the current source file.
     * @param filename The file name.
     */
    public void setSourceCodeFilename(String filename) {
	this.sourceCodeFilename = filename;
    }

    /**
     * Get the LanguageVersion associated with the current source file.
     * @return The LanguageVersion, <code>null</code> if unknown.
     */
    public LanguageVersion getLanguageVersion() {
	return this.languageVersion;
    }

    /**
     * Set the LanguageVersion associated with the current source file.
     * This may be set to <code>null</code> to indicate the version is
     * unknown and should be automatically determined.
     *
     * @param languageVersion The LanguageVersion.
     */
    public void setLanguageVersion(LanguageVersion languageVersion) {
	this.languageVersion = languageVersion;
    }

    /**
     * Set an attribute value on the RuleContext, if it does not already exist.
     * <p>
     * Attributes can be shared between RuleContext instances.  This operation
     * is thread-safe.
     * <p>
     * Attribute values should be modified directly via the reference provided.
     * It is not necessary to call <code>setAttribute(String, Object)</code> to
     * update an attribute value.  Modifications made to the attribute value
     * will automatically be seen by other threads.  Because of this, you must
     * ensure the attribute values are themselves thread safe.
     *
     * @param name The attribute name.
     * @param value The attribute value.
     * @exception IllegalArgumentException if <code>name</code> or <code> value</code> are <code>null</code>
     * @return <code>true</code> if the attribute was set, <code>false</code> otherwise.
     */
    public boolean setAttribute(String name, Object value) {
	if (name == null) {
	    throw new IllegalArgumentException("Parameter 'name' cannot be null.");
	}
	if (value == null) {
	    throw new IllegalArgumentException("Parameter 'value' cannot be null.");
	}
	synchronized (this.attributes) {
	    if (!this.attributes.containsKey(name)) {
		this.attributes.put(name, value);
		return true;
	    } else {
		return false;
	    }
	}
    }

    /**
     * Get an attribute value on the RuleContext.
     * <p>
     * Attributes can be shared between RuleContext instances.  This operation
     * is thread-safe.
     * <p>
     * Attribute values should be modified directly via the reference provided.
     * It is not necessary to call <code>setAttribute(String, Object)</code> to
     * update an attribute value.  Modifications made to the attribute value
     * will automatically be seen by other threads.  Because of this, you must
     * ensure the attribute values are themselves thread safe.
     *
     * @param name The attribute name.
     * @return The current attribute value, or <code>null</code> if the attribute does not exist.
     */
    public Object getAttribute(String name) {
	return this.attributes.get(name);
    }

    /**
     * Remove an attribute value on the RuleContext.
     * <p>
     * Attributes can be shared between RuleContext instances.  This operation
     * is thread-safe.
     * <p>
     * Attribute values should be modified directly via the reference provided.
     * It is not necessary to call <code>setAttribute(String, Object)</code> to
     * update an attribute value.  Modifications made to the attribute value
     * will automatically be seen by other threads.  Because of this, you must
     * ensure the attribute values are themselves thread safe.
     *
     * @param name The attribute name.
     * @return The current attribute value, or <code>null</code> if the attribute does not exist.
     */
    public Object removeAttribute(String name) {
	return this.attributes.remove(name);
    }

    /**
     * Configure whether exceptions during applying a rule should be ignored or not.
     * If set to <code>true</code> then such exceptions are logged as warnings and
     * the processing is continued with the next rule - the failing rule is simply skipped.
     * This is the default behavior.
     * <br>
     * If set to <code>false</code> then the processing will be aborted with the exception.
     * This is especially useful during unit tests, in order to not oversee any exceptions.
     * @param ignoreExceptions if <code>true</code> simply skip failing rules (default).
     */
    public void setIgnoreExceptions(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    /**
     * Gets the configuration whether to skip failing rules (<code>true</code>)
     * or whether to throw a a RuntimeException and abort the processing for the first
     * failing rule.
     * @return <code>true</code> when failing rules are skipped, <code>false</code> otherwise.
     */
    public boolean isIgnoreExceptions() {
        return ignoreExceptions;
    }
}
