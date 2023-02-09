/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;


/**
 * @author sergey.gorbaty
 */
public class VfLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Salesforce VisualForce";
    public static final String TERSE_NAME = "vf";

    public VfLanguageModule() {
        super(createMetdata(),
              p -> new VfHandler((VfLanguageProperties) p));
    }

    private static LanguageMetadata createMetdata() {
        LanguageMetadata languageMetadata = LanguageMetadata.withId(TERSE_NAME).name(NAME)
                .extensions("page", "component")
                .dependsOnLanguage(ApexLanguageModule.TERSE_NAME);
        // use the same versions as in Apex
        int lastVersion = ApexLanguageModule.VERSIONS.size() - 1;
        ApexLanguageModule.VERSIONS.subList(0, lastVersion).forEach(languageMetadata::addVersion);
        languageMetadata.addDefaultVersion(ApexLanguageModule.VERSIONS.get(lastVersion));
        return languageMetadata;
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        return new VfLanguageProperties();
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
