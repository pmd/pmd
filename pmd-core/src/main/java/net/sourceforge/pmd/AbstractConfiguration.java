/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.nio.charset.Charset;

/**
 * Base configuration class for both PMD and CPD.
 *
 * @author Brian Remedios
 */
public abstract class AbstractConfiguration {

    private Charset sourceEncoding = Charset.forName(System.getProperty("file.encoding"));

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
    public Charset getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * Set the character encoding of source files.
     *
     * @param sourceEncoding
     *            The character encoding.
     */
    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = Charset.forName(sourceEncoding);
    }

}
