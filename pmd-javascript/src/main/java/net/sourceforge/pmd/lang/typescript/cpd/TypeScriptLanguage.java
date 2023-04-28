/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;

/**
 * @author pguyot@kallisys.net
 */
public class TypeScriptLanguage extends AbstractLanguage {

    public TypeScriptLanguage() {
        super("TypeScript", "typescript", new TypeScriptTokenizer(), ".ts");
    }
}
