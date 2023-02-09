/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;

public class ApexLanguageModule extends LanguageModuleBase {

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";

    public static final List<String> VERSIONS = listOf("52", "53", "54", "55", "56", "57");

    public ApexLanguageModule() {
        super(createMetadata());
    }

    private static LanguageMetadata createMetadata() {
        LanguageMetadata languageMetadata = LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("cls", "trigger");
        int lastVersion = VERSIONS.size() - 1;
        VERSIONS.subList(0, lastVersion).forEach(languageMetadata::addVersion);
        languageMetadata.addDefaultVersion(VERSIONS.get(lastVersion));
        return languageMetadata;
    }

    @Override
    public ApexLanguageProperties newPropertyBundle() {
        return new ApexLanguageProperties();
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new ApexLanguageProcessor((ApexLanguageProperties) bundle);
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
