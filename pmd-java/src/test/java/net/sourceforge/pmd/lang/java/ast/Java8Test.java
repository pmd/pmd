/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.typeresolution.testdata.java8.UsesJavaStreams;
import net.sourceforge.pmd.typeresolution.testdata.java8.UsesRepeatableAnnotations;

public class Java8Test {
    private final JavaParsingHelper java8 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("8")
                                             .withResourceContext(Java8Test.class);

    @Test
    public void interfaceMethodShouldBeParseable() {
        java8.parseClass(UsesJavaStreams.class);
    }

    @Test
    public void repeatableAnnotationsMethodShouldBeParseable() {
        java8.parseClass(UsesRepeatableAnnotations.class);
    }
}
