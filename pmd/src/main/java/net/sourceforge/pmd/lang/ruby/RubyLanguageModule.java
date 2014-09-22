package net.sourceforge.pmd.lang.ruby;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class RubyLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Ruby";

    public RubyLanguageModule() {
        super(NAME, null, "ruby", null, "rb", "cgi", "class");
        addVersion("", null, true);
    }

}
