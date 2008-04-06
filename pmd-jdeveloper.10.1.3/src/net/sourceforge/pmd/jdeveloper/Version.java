package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.SourceType;

import oracle.ide.Context;

import oracle.jdeveloper.compiler.OjcConfiguration;

class Version {
    public static void setJavaVersion(Context context, PMD pmd) {
        OjcConfiguration config = 
            OjcConfiguration.getInstance(context.getProject());
        String source = config.getSource();
        if (source.equals("1.6")) {
            pmd.setJavaVersion(SourceType.JAVA_16);
        } else if (source.equals("1.5")) {
            pmd.setJavaVersion(SourceType.JAVA_15);
        } else if (source.equals("1.4")) {
            pmd.setJavaVersion(SourceType.JAVA_14);
        } else if (source.equals("1.3")) {
            pmd.setJavaVersion(SourceType.JAVA_13);
        }
    }

    public static String version() {
        return "4.2.2.0.0";
    }

}
