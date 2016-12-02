/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Implements the Go Language
 *
 * @author oinume@gmail.com
 */
public class GoLanguage extends AbstractLanguage {

    /**
     * Creates a new instance of {@link GoLanguage}
     */
    public GoLanguage() {
        super("Go", "go", new GoTokenizer(), ".go");
    }
}
