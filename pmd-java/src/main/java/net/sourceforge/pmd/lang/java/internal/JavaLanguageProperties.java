/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import org.apache.commons.lang3.EnumUtils;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * @author Clément Fournier
 */
public class JavaLanguageProperties extends JvmLanguagePropertyBundle {

    static final PropertyDescriptor<InferenceLoggingVerbosity> INTERNAL_INFERENCE_LOGGING_VERBOSITY =
        PropertyFactory.enumProperty("xTypeInferenceLogging",
                                     EnumUtils.getEnumMap(InferenceLoggingVerbosity.class))
                       .desc("Verbosity of the type inference logging")
                       .defaultValue(InferenceLoggingVerbosity.DISABLED)
                       .build();

    public JavaLanguageProperties() {
        super(JavaLanguageModule.getInstance());
        definePropertyDescriptor(INTERNAL_INFERENCE_LOGGING_VERBOSITY);
    }

    public static boolean isPreviewEnabled(LanguageVersion version) {
        return version.getVersion().endsWith("-preview");
    }

    public static int getInternalJdkVersion(LanguageVersion version) {
        // Todo that's ugly..
        String verString = version.getVersion();
        if (isPreviewEnabled(version)) {
            verString = verString.substring(0, verString.length() - "-preview".length());
        }
        if (verString.startsWith("1.")) {
            verString = verString.substring(2);
        }

        return Integer.parseInt(verString);
    }

    public enum InferenceLoggingVerbosity {
        DISABLED, SIMPLE, VERBOSE
    }
}
