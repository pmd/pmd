/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.pom;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;

public class PomLanguageModule extends SimpleDialectLanguageModuleBase {
    private static final String ID = "pom";

    public PomLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Maven POM")
                              .extensions("pom")
                              .addDefaultVersion("4.0.0")
                              .asDialectOf("xml"));
    }

    public static PomLanguageModule getInstance() {
        return (PomLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

}
