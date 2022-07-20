/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.pom;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.xml.XmlHandler;
import net.sourceforge.pmd.processor.SimpleBatchLanguageProcessor;

public class PomLanguageModule extends BaseLanguageModule {
    public static final String NAME = "Maven POM";
    public static final String TERSE_NAME = "pom";

    public PomLanguageModule() {
        super(NAME, null, TERSE_NAME, "pom");
        addVersion("", new XmlHandler(), true);
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new SimpleBatchLanguageProcessor(bundle, new XmlHandler());
    }
}
