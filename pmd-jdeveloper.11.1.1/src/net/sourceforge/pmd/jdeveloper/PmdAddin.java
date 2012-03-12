package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Addin;
import oracle.ide.ExtensionRegistry;
import oracle.ide.Ide;
import oracle.ide.config.IdeSettings;
import oracle.ide.extension.RegisteredByExtension;
import oracle.ide.panels.Navigable;

@RegisteredByExtension("net.sourceforge.pmd.jdeveloper")
public class PmdAddin implements Addin {

    public static final String PMD_TITLE = "PMD";
    public static boolean added = false;
    public static PmdViolationPage pmdViolationPage;
    
    @Override
    public void initialize() {
        Ide.getVersionInfo().addComponent(PMD_TITLE, "JDeveloper Extension " + Version.version());
        pmdViolationPage = new PmdViolationPage();
    }

}
