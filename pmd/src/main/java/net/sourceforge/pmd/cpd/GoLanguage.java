/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

/**
 * GoLang
 *
 * @author oinume@gmail.com
 */
public class GoLanguage extends AbstractLanguage {
    public GoLanguage() {
        super(new GoTokenizer(), ".go");
    }
}
