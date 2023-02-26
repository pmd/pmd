/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.pom;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.XmlHandler;

public class PomLanguageModule extends SimpleLanguageModuleBase {

    static final String NAME = "Maven POM";
    static final String TERSE_NAME = "pom";
    private static final PomLanguageModule INSTANCE = new PomLanguageModule();

    public PomLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions("pom")
                              .addDefaultVersion("4.0.0"),
              new XmlHandler());
    }

    public static PomLanguageModule getInstance() {
        return INSTANCE;
    }

}
