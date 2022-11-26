/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageHandler;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JavaLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Java";
    public static final String TERSE_NAME = "java";

    public JavaLanguageModule() {
        super(NAME, null, TERSE_NAME, "java");
        addVersion("1.3", new JavaLanguageHandler(3));
        addVersion("1.4", new JavaLanguageHandler(4));
        addVersion("1.5", new JavaLanguageHandler(5), "5");
        addVersion("1.6", new JavaLanguageHandler(6), "6");
        addVersion("1.7", new JavaLanguageHandler(7), "7");
        addVersion("1.8", new JavaLanguageHandler(8), "8");
        addVersion("9", new JavaLanguageHandler(9), "1.9");
        addVersion("10", new JavaLanguageHandler(10), "1.10");
        addVersion("11", new JavaLanguageHandler(11));
        addVersion("12", new JavaLanguageHandler(12));
        addVersion("13", new JavaLanguageHandler(13));
        addVersion("14", new JavaLanguageHandler(14));
        addVersion("15", new JavaLanguageHandler(15));
        addVersion("16", new JavaLanguageHandler(16));
        addVersion("17", new JavaLanguageHandler(17));
        addVersion("18", new JavaLanguageHandler(18));
        addVersion("18-preview", new JavaLanguageHandler(18, true));
        addDefaultVersion("19", new JavaLanguageHandler(19)); // 19 is the default
        addVersion("19-preview", new JavaLanguageHandler(19, true));
    }

    public static Language getInstance() {
        return LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
