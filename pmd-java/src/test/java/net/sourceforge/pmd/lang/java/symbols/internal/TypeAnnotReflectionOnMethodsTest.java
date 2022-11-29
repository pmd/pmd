/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.aAndBAnnot;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.aAnnot;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.assertHasTypeAnnots;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.bAnnot;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.getMethodType;
import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsOnMethods;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
public class TypeAnnotReflectionOnMethodsTest {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;


    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnParameter(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsOnMethods.class);

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


    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnReturn(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract @A @B String abOnReturn(@A String i);
         */

        {
            JMethodSig t = getMethodType(sym, "abOnReturn");
            assertHasTypeAnnots(t.getFormalParameters().get(0), aAnnot);
            assertHasTypeAnnots(t.getReturnType(), aAndBAnnot);
        }
    }

    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnThrows(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract void aOnThrows() throws @A RuntimeException;
         */

        {
            JMethodSig t = getMethodType(sym, "aOnThrows");
            assertHasTypeAnnots(t.getReturnType(), emptyList());
            assertHasTypeAnnots(t.getThrownExceptions().get(0), aAnnot);
        }
    }

    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnTParam(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsOnMethods.class);

        /*

    abstract <@A @B T, E extends T> void abOnTypeParm();
    abstract <@A @B T, E extends T> T abOnTypeParm2(T t);
    abstract <@A T, E extends @B T> void bOnTypeParmBound();
    abstract <@A T, E extends @B T> E bOnTypeParmBound(T t);


         */

        {
            JMethodSig t = getMethodType(sym, "abOnTypeParm");
            assertHasTypeAnnots(t.getTypeParameters().get(0), aAndBAnnot);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
        }
        {
            JMethodSig t = getMethodType(sym, "abOnTypeParm2");
            assertHasTypeAnnots(t.getTypeParameters().get(0), aAndBAnnot);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
            assertHasTypeAnnots(t.getReturnType(), emptyList());
            assertHasTypeAnnots(t.getFormalParameters().get(0), emptyList());
        }
    }

    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnTParamBound(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract <@A T, E extends @B T> void bOnTypeParmBound();
            abstract <@A T, E extends @B T> E bOnTypeParmBound(T t);
            abstract <@A T, E extends @B Cloneable & @A Serializable> E bOnTypeParmBoundIntersection(T t);
         */

        {
            JMethodSig t = getMethodType(sym, "bOnTypeParmBound");
            assertHasTypeAnnots(t.getTypeParameters().get(0), aAnnot);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
            assertHasTypeAnnots(t.getTypeParameters().get(1).getUpperBound(), bAnnot);
        }
        {
            JMethodSig t = getMethodType(sym, "bOnTypeParmBound");
            assertHasTypeAnnots(t.getTypeParameters().get(0), aAnnot);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
            assertHasTypeAnnots(t.getTypeParameters().get(1).getUpperBound(), bAnnot);

            assertHasTypeAnnots(t.getReturnType(), emptyList());
            assertHasTypeAnnots(t.getFormalParameters().get(0), aAnnot); // this is inherited from the declaration
        }
        {
            JMethodSig t = getMethodType(sym, "bOnTypeParmBoundIntersection");
            assertHasTypeAnnots(t.getTypeParameters().get(0), aAnnot);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
            assertHasTypeAnnots(t.getFormalParameters().get(0), aAnnot); // this is inherited from the declaration

            JIntersectionType ub = (JIntersectionType) t.getTypeParameters().get(1).getUpperBound();
            assertHasTypeAnnots(ub, emptyList());
            assertHasTypeAnnots(ub.getComponents().get(0), bAnnot);
            assertHasTypeAnnots(ub.getComponents().get(1), aAnnot);
        }
    }

    @ParameterizedTest
    @EnumSource
    public void testTypeAnnotOnReceiver(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ts, ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract void abOnReceiver(@A @B ClassWithTypeAnnotationsOnMethods this);
         */

        {
            JMethodSig t = getMethodType(sym, "abOnReceiver");
            assertThat(t.getFormalParameters(), Matchers.empty());
            assertHasTypeAnnots(t.getAnnotatedReceiverType(), aAndBAnnot);
        }
    }


}
