/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.pom;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.XmlHandler;

public class PomLanguageModule extends SimpleLanguageModuleBase {
    public static final String NAME = "Maven POM";
    public static final String TERSE_NAME = "pom";

    public PomLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("pom"), new XmlHandler());
    }

}
