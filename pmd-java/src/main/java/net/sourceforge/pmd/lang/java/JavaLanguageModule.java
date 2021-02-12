/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.BaseLanguageModule;

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
        addVersion("14-preview", new JavaLanguageHandler(14, true));
        addVersion("15", new JavaLanguageHandler(15));
        addVersion("15-preview", new JavaLanguageHandler(15, true));
        addDefaultVersion("16", new JavaLanguageHandler(16)); // 16 is the default
        addVersion("16-preview", new JavaLanguageHandler(16, true));
    }

}
