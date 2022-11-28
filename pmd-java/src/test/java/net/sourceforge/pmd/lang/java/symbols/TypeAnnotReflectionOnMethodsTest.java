/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static net.sourceforge.pmd.lang.java.symbols.TypeAnnotReflectionTest.assertHasTypeAnnots;
import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.TypeAnnotReflectionTest.AnnotAImpl;
import net.sourceforge.pmd.lang.java.symbols.TypeAnnotReflectionTest.AnnotBImpl;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsOnMethods;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
public class TypeAnnotReflectionOnMethodsTest {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;

    JClassType sym = (JClassType) ts.declaration(ts.getClassSymbol(ClassWithTypeAnnotationsOnMethods.class));

    private final List<Annotation> aAnnot = listOf(new AnnotAImpl());
    private final List<Annotation> bAnnot = listOf(new AnnotBImpl());
    private final List<Annotation> aAndBAnnot = listOf(new AnnotAImpl(), new AnnotBImpl());


    @Test
    public void testTypeAnnotOnParameter() {

        /*
            abstract void aOnIntParam(@A int i);

            abstract void aOnStringParam(@A String i);
         */

        {
            JMethodSig t = getMethodType(sym, "aOnIntParam");
            assertHasTypeAnnots(t.getFormalParameters().get(0), aAnnot);
            assertHasTypeAnnots(t.getReturnType(), emptyList());
        }
        {
            JMethodSig t = getMethodType(sym, "aOnStringParam");
            assertHasTypeAnnots(t.getFormalParameters().get(0), aAnnot);
            assertHasTypeAnnots(t.getReturnType(), emptyList());
        }
    }


    @Test
    public void testTypeAnnotOnReturn() {

        /*
            abstract @A @B String abOnReturn(@A String i);
         */

        {
            JMethodSig t = getMethodType(sym, "abOnReturn");
            assertHasTypeAnnots(t.getFormalParameters().get(0), aAnnot);
            assertHasTypeAnnots(t.getReturnType(), aAndBAnnot);
        }
    }

    private static JMethodSig getMethodType(JClassType sym, String fieldName) {
        return sym.streamMethods(it -> it.nameEquals(fieldName)).findFirst().get();
    }

}
