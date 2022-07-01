/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.gherkin.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;

/**
 * Language implementation for Gherkin.
 */
public class GherkinLanguage extends AbstractLanguage {

    /**
     * Creates a new Gherkin Language instance.
     */
    public GherkinLanguage() {
        super("Gherkin", "gherkin", new GherkinTokenizer(), ".feature");
    }
}
