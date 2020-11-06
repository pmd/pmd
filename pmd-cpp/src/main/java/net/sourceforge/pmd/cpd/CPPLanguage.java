/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

/**
 * Defines the Language module for C/C++
 */
public class CPPLanguage extends AbstractLanguage {

    /**
     * Creates a new instance of {@link CPPLanguage} with the default extensions
     * for c/c++ files.
     */
    public CPPLanguage() {
        this(System.getProperties());
    }

    public CPPLanguage(Properties properties) {
        super("C++", "cpp", new CPPTokenizer(), ".h", ".hpp", ".hxx", ".c", ".cpp", ".cxx", ".cc", ".C");
        setProperties(properties);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.pmd.cpd.AbstractLanguage#setProperties(java.util.
     * Properties)
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        ((CPPTokenizer) getTokenizer()).setProperties(properties);
    }
}
