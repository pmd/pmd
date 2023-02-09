/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XmlLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "XML";
    public static final String TERSE_NAME = "xml";
    @InternalApi
    public static final List<String> EXTENSIONS = listOf("xml");

    public XmlLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions(EXTENSIONS.get(0))
                              .addVersion("1.0")
                              .addDefaultVersion("1.1"),
                new XmlHandler());
    }
}
