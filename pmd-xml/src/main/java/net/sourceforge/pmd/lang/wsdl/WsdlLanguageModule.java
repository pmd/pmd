/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.wsdl;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlHandler;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleChainVisitor;

/**
 * Created by bernardo-macedo on 24.06.15.
 */
public class WsdlLanguageModule extends BaseLanguageModule {
    public static final String NAME = "WSDL";
    public static final String TERSE_NAME = "wsdl";

    public WsdlLanguageModule() {
        super(NAME, null, TERSE_NAME, XmlRuleChainVisitor.class, "wsdl");
        addVersion("", new XmlHandler(), true);
    }

}
