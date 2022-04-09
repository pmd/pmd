/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Language;

import apex.jorje.services.Version;

public class ApexLanguageModule extends BaseLanguageModule {

    public static final ApexLanguageModule INSTANCE = new ApexLanguageModule();

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";

    private ApexLanguageModule() {
        super(NAME, null, TERSE_NAME, listOf("cls", "trigger"));
        addVersion(String.valueOf((int) Version.CURRENT.getExternal()), new ApexHandler(), true);
    }

    // fixme check syntax of ServiceLoader
    public static Language provide() {
        return INSTANCE;
    }
}
