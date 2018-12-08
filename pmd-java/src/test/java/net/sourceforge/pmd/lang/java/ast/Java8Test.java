/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.typeresolution.testdata.java8.UsesJavaStreams;
import net.sourceforge.pmd.typeresolution.testdata.java8.UsesRepeatableAnnotations;

public class Java8Test {
    @Test
    public void interfaceMethodShouldBeParseable() {
        ParserTstUtil.parseJava18(UsesJavaStreams.class);
    }

    @Test
    public void repeatableAnnotationsMethodShouldBeParseable() {
        ParserTstUtil.parseJava18(UsesRepeatableAnnotations.class);
    }
}
