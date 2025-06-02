/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.scala.cpd.ScalaCpdLexer;

/**
 * Language Module for Scala.
 */
public class ScalaLanguageModule extends SimpleLanguageModuleBase {
    private static final String ID = "scala";

    /**
     * Create a new instance of Scala Language Module.
     */
    public ScalaLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Scala")
                              .extensions("scala")
                              .addVersion("2.10")
                              .addVersion("2.11")
                              .addVersion("2.12")
                              .addDefaultVersion("2.13"),
              new ScalaLanguageHandler());
    }

    public static ScalaLanguageModule getInstance() {
        return (ScalaLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new ScalaCpdLexer(bundle);
    }
}
