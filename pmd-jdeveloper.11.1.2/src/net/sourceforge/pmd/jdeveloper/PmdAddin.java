package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Addin;
import oracle.ide.ExtensionRegistry;
import oracle.ide.Ide;
import oracle.ide.extension.RegisteredByExtension;

@RegisteredByExtension("net.sourceforge.pmd.jdeveloper")
public class PmdAddin implements Addin {

    public static final String PMD_TITLE = "PMD";
    public static boolean added = false;
    
    @Override
    public void initialize() {
        ExtensionRegistry.getExtensionRegistry().invokeAfterExtensionLoading(new Runnable() {
                public void run() {
                    // IdeSettings.registerUI(new Navigable(PMD_TITLE, SettingsPanel.class, new Navigable[] { }));
                    Ide.getVersionInfo().addComponent(PMD_TITLE, "JDeveloper Extension " + Version.version());
                }
            }, true);
    }

}
