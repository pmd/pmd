package net.sourceforge.pmd.jdeveloper;

import java.util.List;

import oracle.jdeveloper.compiler.CompilerPage;


public class PmdViolationPage extends CompilerPage {

    public PmdViolationPage() {
        super(PmdAddin.PMD_TITLE, PmdAddin.PMD_TITLE, null);
    }

    public void add(final List list) {
        super.logMsg(list);
    }
}
