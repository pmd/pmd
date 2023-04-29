/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.julia.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;

/**
 * Language implementation for Julia.
 */
public class JuliaLanguage extends AbstractLanguage {

    /**
     * Creates a new Julia Language instance.
     */
    public JuliaLanguage() {
        super("Julia", "julia", new JuliaTokenizer(), ".jl");
    }
}
