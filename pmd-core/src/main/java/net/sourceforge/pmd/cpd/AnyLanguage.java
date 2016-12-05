/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

public class AnyLanguage extends AbstractLanguage {
    public AnyLanguage(String... extensions) {
        super("Any Language", "any", new AnyTokenizer(), extensions);
    }
}
