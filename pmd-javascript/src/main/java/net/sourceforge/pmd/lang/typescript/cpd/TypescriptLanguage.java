/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;

/**
 * @author pguyot@kallisys.net
 */
public class TypescriptLanguage extends AbstractLanguage {

    public TypescriptLanguage() {
        super("Typescript", "typescript", new TypescriptTokenizer(), ".ts");
    }
}
