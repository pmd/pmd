package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.SourceType;

import oracle.ide.Context;
import oracle.ide.Ide;

import oracle.jdeveloper.compiler.BuildSystemConfiguration;


final class Version {
    private Version() {
    }

    public static void setJavaVersion(final Context context, final PMD pmd) {
        final BuildSystemConfiguration config =
            BuildSystemConfiguration.getInstance(context.getProject());
        final String source = config.getSource();
        if ("1.6".equals(source)) {
            pmd.setJavaVersion(SourceType.JAVA_16);
        } else if ("1.5".equals(source)) {
            pmd.setJavaVersion(SourceType.JAVA_15);
        } else if ("1.4".equals(source)) {
            pmd.setJavaVersion(SourceType.JAVA_14);
        } else if ("1.3".equals(source)) {
            pmd.setJavaVersion(SourceType.JAVA_13);
        }
    }

    public static String version() {
        return "11.1.1.4300";
    }

    public static String getJdevHome() {
        return Ide.getProductHomeDirectory();
    }
}
