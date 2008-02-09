/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuleContext {

	private Report report = new Report();
	private File sourceCodeFile;
	private String sourceCodeFilename;
	private SourceType sourceType;
	private final Map<String, Object> attributes;

	/**
	 * Default constructor.
	 */
	public RuleContext() {
		attributes = Collections.synchronizedMap(new HashMap<String, Object>());
	}

	/**
	 * Constructor which shares attributes with the given RuleContext.
	 */
	public RuleContext(RuleContext ruleContext) {
		this.attributes = ruleContext.attributes;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public File getSourceCodeFile() {
		return sourceCodeFile;
	}

	public void setSourceCodeFile(File sourceCodeFile) {
		this.sourceCodeFile = sourceCodeFile;
	}

	public String getSourceCodeFilename() {
		return sourceCodeFilename;
	}

	public void setSourceCodeFilename(String filename) {
		this.sourceCodeFilename = filename;
	}

	public void excludeLines(Map<Integer, String> lines) {
		report.exclude(lines);
	}

	public SourceType getSourceType() {
		return this.sourceType;
	}

	public void setSourceType(SourceType t) {
		this.sourceType = t;
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
}
