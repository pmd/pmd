/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleChainVisitor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JavaLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Java";
    public static final String TERSE_NAME = "java";

    public JavaLanguageModule() {
        super(NAME, null, TERSE_NAME, JavaRuleChainVisitor.class, "java");
        addVersion("1.3", new JavaLanguageHandler(3), false);
        addVersion("1.4", new JavaLanguageHandler(4), false);
        addVersion("1.5", new JavaLanguageHandler(5), false, "5");
        addVersion("1.6", new JavaLanguageHandler(6), false, "6");
        addVersion("1.7", new JavaLanguageHandler(7), false, "7");
        addVersion("1.8", new JavaLanguageHandler(8), false, "8");
        addVersion("9", new JavaLanguageHandler(9), false, "1.9");
        addVersion("10", new JavaLanguageHandler(10), false, "1.10");
        addVersion("11", new JavaLanguageHandler(11), false);
        addVersion("12", new JavaLanguageHandler(12), false);
        addVersion("13", new JavaLanguageHandler(13), false);
        addVersion("13-preview", new JavaLanguageHandler(13, true), false);
        addVersion("14", new JavaLanguageHandler(14), true); // 14 is the default
        addVersion("14-preview", new JavaLanguageHandler(14, true), false);
    }

}
