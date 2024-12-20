/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rust;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rust.cpd.RustCpdLexer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

public class RustLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "rust";

    public RustLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Rust").extensions("rust"));
    }

    public static RustLanguageModule getInstance() {
        return (RustLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new RustCpdLexer();
    }
  
}
