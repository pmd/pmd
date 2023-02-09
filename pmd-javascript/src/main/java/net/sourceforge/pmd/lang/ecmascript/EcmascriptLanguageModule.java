/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class EcmascriptLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "JavaScript";
    public static final String TERSE_NAME = "ecmascript";
    @InternalApi
    public static final List<String> EXTENSIONS = listOf("js");

    public EcmascriptLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions(EXTENSIONS.get(0))
                              .addVersion("3")
                              .addVersion("5")
                              .addVersion("6", "ES6", "ES2015")
                              .addVersion("7", "ES2016")
                              .addVersion("8", "ES2017")
                              .addDefaultVersion("9", "ES2018"),
              properties -> () -> new EcmascriptParser(properties));
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
