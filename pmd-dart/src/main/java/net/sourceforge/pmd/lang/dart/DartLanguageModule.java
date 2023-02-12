/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dart;

import net.sourceforge.pmd.lang.dart.cpd.DartTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

/**
 * Language implementation for Dart
 */
public class DartLanguageModule extends CpdOnlyLanguageModuleBase {

    /**
     * Creates a new Dart Language instance.
     */
    public DartLanguageModule() {
        super(LanguageMetadata.withId("dart").name("Dart").extensions("dart"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new DartTokenizer();
    }
}
