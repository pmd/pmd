/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser;
import net.sourceforge.pmd.lang.ecmascript.cpd.EcmascriptTokenizer;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class EcmascriptLanguageModule extends SimpleLanguageModuleBase {

    static final String NAME = "JavaScript";
    static final String TERSE_NAME = "ecmascript";
    private static final EcmascriptLanguageModule INSTANCE = new EcmascriptLanguageModule();

    public EcmascriptLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("js")
                              .addVersion("3")
                              .addVersion("5")
                              .addVersion("6", "ES6", "ES2015")
                              .addVersion("7", "ES2016")
                              .addVersion("8", "ES2017")
                              .addDefaultVersion("9", "ES2018"),
              properties -> () -> new EcmascriptParser(properties));
    }

    public static EcmascriptLanguageModule getInstance() {
        return INSTANCE;
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new EcmascriptTokenizer();
    }
}
