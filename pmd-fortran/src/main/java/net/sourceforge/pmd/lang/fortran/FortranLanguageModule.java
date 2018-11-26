/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.fortran;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Implementation for the Fortran Language Module
 *
 * @deprecated There is no full PMD support for fortran.
 */
@Deprecated
public class FortranLanguageModule extends BaseLanguageModule {

    /** The name */
    public static final String NAME = "Fortran";
    /** The terse name */
    public static final String TERSE_NAME = "fortran";

    /**
     * Creates a new instance of {@link FortranLanguageModule}
     */
    public FortranLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "for", "f", "f66", "f77", "f90");
        addVersion("", null, true);
    }

}
