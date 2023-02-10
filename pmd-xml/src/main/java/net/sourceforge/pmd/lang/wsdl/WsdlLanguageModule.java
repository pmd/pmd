/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.wsdl;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.XmlHandler;

/**
 * Created by bernardo-macedo on 24.06.15.
 */
public class WsdlLanguageModule extends SimpleLanguageModuleBase {
    public static final String NAME = "WSDL";
    public static final String TERSE_NAME = "wsdl";

    public WsdlLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("wsdl"), new XmlHandler());
    }

}
