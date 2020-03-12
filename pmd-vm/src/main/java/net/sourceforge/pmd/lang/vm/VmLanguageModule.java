/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.vm.rule.VmRuleChainVisitor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class VmLanguageModule extends BaseLanguageModule {

    public static final String NAME = "VM";
    public static final String TERSE_NAME = "vm";

    public VmLanguageModule() {
        super(NAME, null, TERSE_NAME, VmRuleChainVisitor.class, "vm");
        addVersion("", new VmHandler(), true);
    }

}
