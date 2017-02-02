/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Defines the Language module for Python
 */
public class PythonLanguage extends AbstractLanguage {

    /**
     * Creates a new instance of {@link PythonLanguage} with the default
     * extensions for python files.
     */
    public PythonLanguage() {
        super("Python", "python", new PythonTokenizer(), ".py");
    }
}
