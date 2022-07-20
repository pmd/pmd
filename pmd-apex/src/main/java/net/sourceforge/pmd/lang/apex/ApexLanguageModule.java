/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;

import apex.jorje.services.Version;

public class ApexLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";

    public ApexLanguageModule() {
        super(NAME, null, TERSE_NAME, listOf("cls", "trigger"));
        addVersion(String.valueOf((int) Version.CURRENT.getExternal()),
                   new ApexLanguageProcessor(new ApexLanguageProperties(this)), true);
    }

    @Override
    public ApexLanguageProperties newPropertyBundle() {
        return new ApexLanguageProperties(this);
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new ApexLanguageProcessor((ApexLanguageProperties) bundle);
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
