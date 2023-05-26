/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.coco;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.coco.cpd.CocoTokenizer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Language implementation for Coco.
 */
public class CocoLanguageModule extends CpdOnlyLanguageModuleBase {

    public CocoLanguageModule() {
        super(LanguageMetadata.withId("coco").name("Coco").extensions("coco"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new CocoTokenizer();
    }

    public static CocoLanguageModule getInstance() {
        return (CocoLanguageModule) LanguageRegistry.CPD.getLanguageById("coco");
    }
}
