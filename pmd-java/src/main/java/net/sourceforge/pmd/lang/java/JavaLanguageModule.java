/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.PmdCapableLanguage;
import net.sourceforge.pmd.lang.java.cpd.JavaCpdLexer;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProcessor;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JavaLanguageModule extends LanguageModuleBase implements PmdCapableLanguage, CpdCapableLanguage {
    private static final String ID = "java";
    static final String NAME = "Java";

    public JavaLanguageModule() {
        super(LanguageMetadata.withId(ID).name(NAME).extensions("java")
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
                              .addVersion("20")
                              .addVersion("21")
                              .addVersion("22")
                              .addVersion("22-preview")
                              .addDefaultVersion("23") // 23 is the default
                              .addVersion("23-preview"));
    }

    public static JavaLanguageModule getInstance() {
        return (JavaLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        return new JavaLanguageProperties();
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new JavaLanguageProcessor((JavaLanguageProperties) bundle);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new JavaCpdLexer((JavaLanguageProperties) bundle);
    }
}
