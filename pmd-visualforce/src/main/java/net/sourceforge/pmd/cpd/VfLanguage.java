/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.vf.VfLanguageModule;

/**
 * @author sergey.gorbaty
 *
 */
public class VfLanguage extends AbstractLanguage {
    public VfLanguage() {
        super(VfLanguageModule.NAME, VfLanguageModule.TERSE_NAME, new VfTokenizer(), VfLanguageModule.EXTENSIONS);
    }
}
