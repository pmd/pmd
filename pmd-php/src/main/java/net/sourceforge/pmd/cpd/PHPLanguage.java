/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Language implementation for PHP
 */
public class PHPLanguage extends AbstractLanguage {

    /**
     * Creates a new PHP Language instance.
     */
    public PHPLanguage() {
        super("PHP", "php", new PHPTokenizer(), ".php", ".class");
    }
}
