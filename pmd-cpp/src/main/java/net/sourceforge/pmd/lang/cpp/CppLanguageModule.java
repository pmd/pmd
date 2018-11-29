/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Implementation of the C/C++ Language Module.
 *
 * @deprecated There is no full PMD support for c++.
 */
@Deprecated
public class CppLanguageModule extends BaseLanguageModule {

    /** The name, that can be used to display the language in UI. */
    public static final String NAME = "C++";
    /** The internal name. */
    public static final String TERSE_NAME = "cpp";

    /**
     * Creates a new instance of {@link CppLanguageModule} with the default file
     * extensions for C++.
     */
    public CppLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "h", "c", "cpp", "cxx", "cc", "C");
        addVersion("", new CppHandler(), true);
    }
}
