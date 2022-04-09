/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Language implementation for Fortran
 *
 * @author Romain PELISSE belaran@gmail.com
 */
public class FortranLanguage extends AbstractLanguage {
    /**
     * Create a Fortran Language instance.
     */
    public FortranLanguage() {
        super("Fortran", "fortran", new AnyTokenizer("!"), ".for", ".f", ".f66", ".f77", ".f90");
    }
}
