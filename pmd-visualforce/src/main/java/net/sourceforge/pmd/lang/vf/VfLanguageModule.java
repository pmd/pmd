/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * @author sergey.gorbaty
 */
public class VfLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Salesforce VisualForce";
    public static final String TERSE_NAME = "vf";
    @InternalApi
    public static final List<String> EXTENSIONS = listOf("page", "component");

    public VfLanguageModule() {
        super(createMetdata(),
              p -> new VfHandler((VfLanguageProperties) p));
    }

    private static LanguageMetadata createMetdata() {
        LanguageMetadata languageMetadata =
                LanguageMetadata.withId(TERSE_NAME).name(NAME)
                                .extensions(EXTENSIONS)
                                .dependsOnLanguage(ApexLanguageModule.TERSE_NAME);
        // use the same versions as in Apex
        // need to create a temporary apex module, since the global language registry is not initialized yet
        ApexLanguageModule temporaryApexModule = new ApexLanguageModule();
        LanguageVersion defaultApexVersion = temporaryApexModule.getDefaultVersion();
        for (LanguageVersion languageVersion : temporaryApexModule.getVersions()) {
            if (!defaultApexVersion.equals(languageVersion)) {
                languageMetadata.addVersion(languageVersion.getVersion());
            }
        }
        languageMetadata.addDefaultVersion(defaultApexVersion.getVersion());
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
