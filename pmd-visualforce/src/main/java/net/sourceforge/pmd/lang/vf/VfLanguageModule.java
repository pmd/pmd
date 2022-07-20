/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.processor.SimpleBatchLanguageProcessor;


/**
 * @author sergey.gorbaty
 *
 */
public class VfLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Salesforce VisualForce";
    public static final String TERSE_NAME = "vf";

    public VfLanguageModule() {
        super(NAME, "VisualForce", TERSE_NAME, "page", "component");
        addVersion("", new VfHandler(new VfLanguageProperties()), true);
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new SimpleBatchLanguageProcessor(bundle, new VfHandler((VfLanguageProperties) bundle));
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
