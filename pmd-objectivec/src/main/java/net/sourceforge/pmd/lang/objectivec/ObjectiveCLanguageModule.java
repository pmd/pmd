/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.objectivec;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.objectivec.cpd.ObjectiveCTokenizer;

/**
 * Defines the Language module for Objective-C
 */
public class ObjectiveCLanguageModule extends CpdOnlyLanguageModuleBase {

    /**
     * Creates a new instance of {@link ObjectiveCLanguageModule} with the default
     * extensions for Objective-C files.
     */
    public ObjectiveCLanguageModule() {
        super(LanguageMetadata.withId("objectivec").name("Objective-C").extensions("h", "m"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new ObjectiveCTokenizer();
    }
}
