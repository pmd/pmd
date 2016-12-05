/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Language implementation for Groovy
 */
public class GroovyLanguage extends AbstractLanguage {

    /**
     * Creates a new Groovy Language instance.
     */
    public GroovyLanguage() {
        super("Groovy", "groovy", new GroovyTokenizer(), ".groovy");
    }
}
