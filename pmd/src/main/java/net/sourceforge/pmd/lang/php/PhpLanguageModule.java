package net.sourceforge.pmd.lang.php;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class PhpLanguageModule extends BaseLanguageModule {

    public static final String NAME = "PHP: Hypertext Preprocessor";
    public static final String TERSE_NAME = "php";

    public PhpLanguageModule() {
        super(NAME, "PHP", TERSE_NAME, null, "php", "class");
        addVersion("", null, true);
    }

}
