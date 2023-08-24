/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class VmLanguageModule extends SimpleLanguageModuleBase {
    static final String ID = "vm";
    static final String NAME = "VM";

    public VmLanguageModule() {
        super(LanguageMetadata.withId(ID).name(NAME)
                              .extensions("vm")
                              .addVersion("2.0")
                              .addVersion("2.1")
                              .addVersion("2.2")
                              .addDefaultVersion("2.3"),
                new VmHandler());
    }

    public static VmLanguageModule getInstance() {
        return (VmLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

}
