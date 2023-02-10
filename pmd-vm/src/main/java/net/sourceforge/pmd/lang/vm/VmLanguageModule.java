/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class VmLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "VM";
    public static final String TERSE_NAME = "vm";

    public VmLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("vm"), new VmHandler());
    }

}
