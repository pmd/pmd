/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.wsdl;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;

/**
 * Created by bernardo-macedo on 24.06.15.
 */
public class WsdlDialectModule extends SimpleDialectLanguageModuleBase {
    private static final String ID = "wsdl";

    public WsdlDialectModule() {
        super(LanguageMetadata.withId(ID).name("WSDL")
                              .extensions("wsdl")
                              .addVersion("1.1")
                              .addDefaultVersion("2.0")
                              .asDialectOf("xml"));
    }

    public static WsdlDialectModule getInstance() {
        return (WsdlDialectModule) LanguageRegistry.PMD.getLanguageById(ID);
    }
}
