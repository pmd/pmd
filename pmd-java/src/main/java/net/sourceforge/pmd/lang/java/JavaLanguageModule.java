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
        addVersion("1.5", new JavaLanguageHandler(5), false);
        addVersion("1.6", new JavaLanguageHandler(6), false);
        addVersion("1.7", new JavaLanguageHandler(7), false);
        addVersion("1.8", new JavaLanguageHandler(8), false);
        addVersion("9", new JavaLanguageHandler(9), false);
        addVersion("10", new JavaLanguageHandler(10), false);
        addVersion("11", new JavaLanguageHandler(11), true);
    }

}
