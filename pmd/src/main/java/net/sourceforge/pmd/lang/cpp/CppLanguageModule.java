package net.sourceforge.pmd.lang.cpp;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class CppLanguageModule extends BaseLanguageModule {

    public static final String NAME = "C++";

    public CppLanguageModule() {
        super(NAME, null, "cpp", null, "h", "c", "cpp", "cxx", "cc", "C");
        addVersion("", new CppHandler(), true);
    }

}
