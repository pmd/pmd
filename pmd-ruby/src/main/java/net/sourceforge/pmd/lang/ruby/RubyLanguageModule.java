package net.sourceforge.pmd.lang.ruby;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class RubyLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Ruby";
    public static final String TERSE_NAME = "ruby";

    public RubyLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "rb", "cgi", "class");
        addVersion("", null, true);
    }

}
