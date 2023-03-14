/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;

/**
 *
 * @author Zev Blut zb@ubit.com
 */
public class EcmascriptLanguage extends AbstractLanguage {
    public EcmascriptLanguage() {
        super(EcmascriptLanguageModule.NAME, EcmascriptLanguageModule.TERSE_NAME, new EcmascriptTokenizer(),
                EcmascriptLanguageModule.EXTENSIONS);
    }
}
