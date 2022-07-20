/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

/**
 * @author Clément Fournier
 */
public class JavaLanguageProperties extends JvmLanguagePropertyBundle {

    public JavaLanguageProperties() {
        super(JavaLanguageModule.getInstance());
    }

    boolean isPreviewEnabled() {
        return getLanguageVersion().getVersion().endsWith("-preview");
    }

    int getInternalJdkVersion() {
        // Todo that's ugly..
        LanguageVersion version = getLanguageVersion();
        String verString = version.getVersion();
        if (isPreviewEnabled()){
            verString = verString.substring(0, verString.length() - "-preview".length());
        }
        if (verString.startsWith("1."))
            verString = verString.substring(2);

        return Integer.parseInt(verString);
    }

}
