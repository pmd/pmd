/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;

public class ApexLanguageModule extends LanguageModuleBase {

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";

    @InternalApi
    public static final List<String> EXTENSIONS = listOf("cls", "trigger");

    public ApexLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions(EXTENSIONS)
                              .addVersion("52")
                              .addVersion("53")
                              .addVersion("54")
                              .addVersion("55")
                              .addVersion("56")
                              .addVersion("57")
                              .addVersion("58")
                              .addDefaultVersion("59"));
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
