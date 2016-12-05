/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.wsdl.rule;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.wsdl.WsdlLanguageModule;
import net.sourceforge.pmd.lang.xml.rule.AbstractXmlRule;

/**
 * Created by bernardo-macedo on 24.06.15.
 */
public class AbstractWsdlRule extends AbstractXmlRule {

    public AbstractWsdlRule() {
        super(LanguageRegistry.getLanguage(WsdlLanguageModule.NAME));
    }

}
