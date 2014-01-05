/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

/**
 * Base configuration class for both PMD and CPD.
 * 
 * @author Brian Remedios
 */
public abstract class AbstractConfiguration {

    private String sourceEncoding = System.getProperty("file.encoding");
    private boolean debug;

    /**
     * Create a new abstract configuration.
     */
    protected AbstractConfiguration() {
        super();
    }

    /**
     * Get the character encoding of source files.
     * 
     * @return The character encoding.
     */
    public String getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * Set the character encoding of source files.
     * 
     * @param sourceEncoding The character encoding.
     */
    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }

    /**
     * Return the debug indicator. If this value is <code>true</code> then PMD
     * will log debug information.
     * 
     * @return <code>true</code> if debug logging is enabled, <code>false</code>
     *         otherwise.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the debug indicator.
     * 
     * @param debug The debug indicator to set.
     * @see #isDebug()
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}