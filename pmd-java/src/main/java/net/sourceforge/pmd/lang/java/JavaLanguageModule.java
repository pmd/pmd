/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProcessor;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JavaLanguageModule extends LanguageModuleBase {

    public static final String NAME = "Java";
    public static final String TERSE_NAME = "java";
    @InternalApi
    public static final List<String> EXTENSIONS = listOf("java");

    public JavaLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions(EXTENSIONS.get(0))
                              .addVersion("1.3")
                              .addVersion("1.4")
                              .addVersion("1.5", "5")
                              .addVersion("1.6", "6")
                              .addVersion("1.7", "7")
                              .addVersion("1.8", "8")
                              .addVersion("9", "1.9")
                              .addVersion("10", "1.10")
                              .addVersion("11")
                              .addVersion("12")
                              .addVersion("13")
                              .addVersion("14")
                              .addVersion("15")
                              .addVersion("16")
                              .addVersion("17")
                              .addVersion("18")
                              .addVersion("19")
                              .addVersion("19-preview")
                              .addDefaultVersion("20") // 20 is the default
                              .addVersion("20-preview"));
    }


    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        return new JavaLanguageProperties();
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new JavaLanguageProcessor((JavaLanguageProperties) bundle);
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
