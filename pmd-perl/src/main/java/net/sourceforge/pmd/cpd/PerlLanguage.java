/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

public class PerlLanguage extends AbstractLanguage {
    public PerlLanguage() {
        super("Perl", "perl", new PerlTokenizer(), ".pm", ".pl", ".t");
    }
}
