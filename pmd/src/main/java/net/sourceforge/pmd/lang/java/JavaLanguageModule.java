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
        addVersion("1.3", new Java13Handler(), false);
        addVersion("1.4", new Java14Handler(), false);
        addVersion("1.5", new Java15Handler(), false);
        addVersion("1.6", new Java16Handler(), false);
        addVersion("1.7", new Java17Handler(), false);
        addVersion("1.8", new Java18Handler(), true);
    }

}
