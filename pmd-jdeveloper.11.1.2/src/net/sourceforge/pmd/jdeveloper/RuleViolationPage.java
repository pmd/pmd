package net.sourceforge.pmd.jdeveloper;

import java.util.List;

import oracle.jdeveloper.compiler.CompilerPage;


public class RuleViolationPage extends CompilerPage {

    public RuleViolationPage() {
        super(Plugin.PMD_TITLE, Plugin.PMD_TITLE, null);
    }

    public void add(final List list) {
        super.logMsg(list);
    }
}
