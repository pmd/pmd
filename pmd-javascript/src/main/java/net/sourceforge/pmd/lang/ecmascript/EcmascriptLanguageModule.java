/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser;
import net.sourceforge.pmd.lang.ecmascript.cpd.EcmascriptCpdLexer;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class EcmascriptLanguageModule extends SimpleLanguageModuleBase {
    static final String ID = "ecmascript";
    static final String NAME = "JavaScript";

    public EcmascriptLanguageModule() {
        super(LanguageMetadata.withId(ID).name(NAME).extensions("js")
                              .addVersion("3")
                              .addVersion("5")
                              .addVersion("6", "ES6", "ES2015")
                              .addVersion("7", "ES2016")
                              .addVersion("8", "ES2017")
                              .addDefaultVersion("9", "ES2018"),
              properties -> () -> new EcmascriptParser(properties));
    }

    public static EcmascriptLanguageModule getInstance() {
        return (EcmascriptLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new EcmascriptCpdLexer();
    }
}
