/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.wsdl;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;

/**
 * Created by bernardo-macedo on 24.06.15.
 *
 * <p>Since PMD 7.13.0 this is a dialect of XML. Before that, WSDL was a language module on its own.
 * @since 7.13.0
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
