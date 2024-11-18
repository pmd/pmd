/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
import net.sourceforge.pmd.annotation.ModuleTestExperimental;

/**
 * Provides a sample module to be used for parsing in net.sourceforge.pmd.lang.java.symbols.internal.asm.ModuleStubTest.
 *
 * <p>Compile with "javac module-info.java ModuleTestExperimental.java ModuleTestPMD.java ModuleTestLanguage.java"
 */
@ModuleTestExperimental
module test.net.sourceforge.pmd {
    exports net.sourceforge.pmd;
    exports net.sourceforge.pmd.annotation;
    exports net.sourceforge.pmd.lang;
}
