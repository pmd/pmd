/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp;

import net.sourceforge.pmd.cpd.CPPTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Defines the Language module for C/C++
 */
public class CppLanguageModule extends CpdOnlyLanguageModuleBase {


    public static final PropertyDescriptor<String> CPD_SKIP_BLOCKS =
        PropertyFactory.stringProperty("cpdSkipBlocksPattern")
                       .defaultValue("#if 0|#endif")
                       .desc("Specifies a start and end delimiter for CPD to completely ignore. "
                                 + "The delimiters are separated by a pipe |. The default skips code "
                                 + " that is conditionally compiled out. Set this property to empty to disable this.")
                       .build();

    /**
     * Creates a new instance of {@link CppLanguageModule} with the default extensions
     * for c/c++ files.
     */
    public CppLanguageModule() {
        super(LanguageMetadata.withId("cpp")
                              .name("C++")
                              .addDefaultVersion("any")
                              .extensions("h", "hpp", "hxx", "c", "cpp", "cxx", "cc", "C"));
    }

    public static CppLanguageModule getInstance() {
        return (CppLanguageModule) LanguageRegistry.CPD.getLanguageById("cpp");
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        LanguagePropertyBundle bundle = super.newPropertyBundle();
        bundle.definePropertyDescriptor(Tokenizer.CPD_IGNORE_LITERAL_SEQUENCES);
        bundle.definePropertyDescriptor(Tokenizer.CPD_IGNORE_LITERAL_AND_IDENTIFIER_SEQUENCES);
        bundle.definePropertyDescriptor(CPD_SKIP_BLOCKS);
        return bundle;
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new CPPTokenizer(bundle);
    }
}
