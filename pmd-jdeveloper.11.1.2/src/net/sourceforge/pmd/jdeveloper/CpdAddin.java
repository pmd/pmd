package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Addin;
import oracle.ide.extension.RegisteredByExtension;

@RegisteredByExtension("net.sourceforge.pmd.jdeveloper")
public class CpdAddin implements Addin {

    public static final String CPD_TITLE = "CPD";

    @Override
    public void initialize() {
        // Nothing to do at the moment
    }

}
