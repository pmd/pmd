/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0

package net.sourceforge.pmd.cpd;

/**
 *
 * @author Zev Blut zb@ubit.com
 */
public class EcmascriptLanguage extends AbstractLanguage {
    public EcmascriptLanguage() {
        super("JavaScript", "ecmascript", new EcmascriptTokenizer(), ".js");
    }
}
