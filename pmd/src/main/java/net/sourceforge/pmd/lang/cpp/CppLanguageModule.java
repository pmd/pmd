package net.sourceforge.pmd.lang.cpp;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class CppLanguageModule extends BaseLanguageModule {

    public static final String NAME = "C++";
    public static final String TERSE_NAME = "cpp";

    public CppLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "h", "c", "cpp", "cxx", "cc", "C");
        addVersion("", new CppHandler(), true);
    }

}
