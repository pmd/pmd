/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.pom;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;

/**
 * Since PMD 7.13.0 this is a dialect of XML. Before that, POM was a language module on its own.
 * @since 7.13.0
 */
public class PomDialectModule extends SimpleDialectLanguageModuleBase {
    private static final String ID = "pom";

    public PomDialectModule() {
        super(LanguageMetadata.withId(ID).name("Maven POM")
                              .extensions("pom")
                              .addDefaultVersion("4.0.0")
                              .asDialectOf("xml"));
    }

    public static PomDialectModule getInstance() {
        return (PomDialectModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

}
