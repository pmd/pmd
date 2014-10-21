/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

/**
 * Language implementation for C#
 */
public class CsLanguage extends AbstractLanguage {

    /**
     * Creates a new C# Language instance.
     */
    public CsLanguage() {
        super("C#", "cs", new CsTokenizer(), ".cs");
    }
}
