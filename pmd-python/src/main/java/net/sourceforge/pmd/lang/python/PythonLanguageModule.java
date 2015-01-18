/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.python;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Implementation of the Python Language Module.
 */
public class PythonLanguageModule extends BaseLanguageModule {

    /** The name, that can be used to display the language in UI. */
    public static final String NAME = "Python";
    /** The internal name. */
    public static final String TERSE_NAME = "python";

    /**
     * Creates a new instance of {@link PythonLanguageModule} with the default file extensions for Python.
     */
    public PythonLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "py");
        addVersion("", new PythonHandler(), true);
    }
}
