/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;


/**
 * @author sergey.gorbaty
 */
public class VfLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Salesforce VisualForce";
    public static final String TERSE_NAME = "vf";

    public VfLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("page", "component"),
              p -> new VfHandler((VfLanguageProperties) p));
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
