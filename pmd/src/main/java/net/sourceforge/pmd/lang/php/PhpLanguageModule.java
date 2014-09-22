package net.sourceforge.pmd.lang.php;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class PhpLanguageModule extends BaseLanguageModule {

    public static final String NAME = "PHP: Hypertext Preprocessor";

    public PhpLanguageModule() {
        super(NAME, "PHP", "php", null, "php", "class");
        addVersion("", null, true);
    }

}
