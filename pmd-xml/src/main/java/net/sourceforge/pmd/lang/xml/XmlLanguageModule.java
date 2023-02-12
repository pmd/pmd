/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.cpd.XmlTokenizer;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XmlLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "XML";
    public static final String TERSE_NAME = "xml";

    public XmlLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("xml"), new XmlHandler());
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new XmlTokenizer();
    }
}
