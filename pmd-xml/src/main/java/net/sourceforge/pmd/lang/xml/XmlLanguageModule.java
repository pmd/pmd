/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.processor.SimpleBatchLanguageProcessor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XmlLanguageModule extends BaseLanguageModule {

    public static final String NAME = "XML";
    public static final String TERSE_NAME = "xml";

    public XmlLanguageModule() {
        super(NAME, null, TERSE_NAME, "xml");
        addVersion("", new XmlHandler(), true);
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new SimpleBatchLanguageProcessor(bundle, new XmlHandler());
    }
}
