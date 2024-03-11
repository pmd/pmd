/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.objectivec;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.objectivec.cpd.ObjectiveCCpdLexer;

/**
 * Defines the Language module for Objective-C
 */
public class ObjectiveCLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "objectivec";

    /**
     * Creates a new instance of {@link ObjectiveCLanguageModule} with the default
     * extensions for Objective-C files.
     */
    public ObjectiveCLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Objective-C").extensions("h", "m"));
    }

    public static ObjectiveCLanguageModule getInstance() {
        return (ObjectiveCLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new ObjectiveCCpdLexer();
    }
}
