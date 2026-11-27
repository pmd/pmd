/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.lang.PmdCapableLanguage;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.xml.ast.XmlRootNode;

/**
 * @author Clément Fournier
 */
public final class XmlParsingHelper extends BaseParsingHelper<XmlParsingHelper, XmlRootNode> {

    public static final XmlParsingHelper XML = new XmlParsingHelper(XmlLanguageModule.getInstance(), Params.getDefault());


    private XmlParsingHelper(PmdCapableLanguage langName, Params params) {
        super(langName, XmlRootNode.class, params);
    }

    @Override
    protected XmlParsingHelper clone(Params params) {
        return new XmlParsingHelper(this.getLanguage(), params);
    }
}
