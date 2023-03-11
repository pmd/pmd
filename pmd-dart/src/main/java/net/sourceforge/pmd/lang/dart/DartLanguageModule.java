/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dart;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.dart.cpd.DartTokenizer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Language implementation for Dart
 */
public class DartLanguageModule extends CpdOnlyLanguageModuleBase {

    public DartLanguageModule() {
        super(LanguageMetadata.withId("dart").name("Dart").extensions("dart")
                              .addDefaultVersion("2"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new DartTokenizer();
    }
}
