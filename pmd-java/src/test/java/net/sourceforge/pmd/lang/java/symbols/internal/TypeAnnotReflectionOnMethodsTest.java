/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.ANNOTS_A_B;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.ANNOT_A;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.ANNOT_B;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.assertHasAnnots;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.assertHasTypeAnnots;
import static net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotTestUtil.getMethodType;
import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsOnMethods;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;

/**
 *
 */
class TypeAnnotReflectionOnMethodsTest {


    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnParameter(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract void aOnIntParam(@A int i);

            abstract void aOnStringParam(@A String i);
         */

        {
            JMethodSig t = getMethodType(sym, "aOnIntParam");
            assertHasTypeAnnots(t.getFormalParameters().get(0), ANNOT_A);
            assertHasTypeAnnots(t.getReturnType(), emptyList());
        }
        {
            JMethodSig t = getMethodType(sym, "aOnStringParam");
            assertHasTypeAnnots(t.getFormalParameters().get(0), ANNOT_A);
            assertHasTypeAnnots(t.getReturnType(), emptyList());
        }
    }


    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnReturn(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract @A @B String abOnReturn(@A String i);
            abstract List<@A String> abOnReturnInArg();
         */

        {
            JMethodSig t = getMethodType(sym, "abOnReturn");
            assertHasTypeAnnots(t.getFormalParameters().get(0), ANNOT_A);
            assertHasTypeAnnots(t.getReturnType(), ANNOTS_A_B);
        }
        {
            JMethodSig t = getMethodType(sym, "abOnReturnInArg");
            assertHasTypeAnnots(((JClassType) t.getReturnType()).getTypeArgs().get(0),
                                ANNOT_A);
        }
    }

    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnThrows(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract void aOnThrows() throws @A RuntimeException;
         */

        {
            JMethodSig t = getMethodType(sym, "aOnThrows");
            assertHasTypeAnnots(t.getReturnType(), emptyList());
            assertHasTypeAnnots(t.getThrownExceptions().get(0), ANNOT_A);
        }
    }

    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnTParam(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsOnMethods.class);

        /*

    abstract <@A @B T, E extends T> void abOnTypeParm();
    abstract <@A @B T, E extends T> T abOnTypeParm2(T t);


         */

        {
            JMethodSig t = getMethodType(sym, "abOnTypeParm");
            assertHasTypeAnnots(t.getTypeParameters().get(0), emptyList());
            assertHasAnnots(t.getTypeParameters().get(0).getSymbol(), ANNOTS_A_B);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
        }
        {
            JMethodSig t = getMethodType(sym, "abOnTypeParm2");
            assertHasTypeAnnots(t.getTypeParameters().get(0), emptyList());
            assertHasAnnots(t.getTypeParameters().get(0).getSymbol(), ANNOTS_A_B);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
            assertHasTypeAnnots(t.getReturnType(), emptyList());
            assertHasTypeAnnots(t.getFormalParameters().get(0), emptyList());
        }
    }

    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnTParamBound(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract <@A T, E extends @B T> E bOnTypeParmBound(T t);
            abstract <@A T, E extends @B Cloneable & @A Serializable> E bOnTypeParmBoundIntersection(T t);
         */

        {
            JMethodSig t = getMethodType(sym, "bOnTypeParmBound");
            assertHasTypeAnnots(t.getTypeParameters().get(0), emptyList());
            assertHasAnnots(t.getTypeParameters().get(0).getSymbol(), ANNOT_A);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
            assertHasTypeAnnots(t.getTypeParameters().get(1).getUpperBound(), ANNOT_B);

            assertHasTypeAnnots(t.getReturnType(), emptyList());
            assertHasTypeAnnots(t.getFormalParameters().get(0), emptyList());
            assertHasAnnots(t.getFormalParameters().get(0).getSymbol(), ANNOT_A);
        }
        {
            JMethodSig t = getMethodType(sym, "bOnTypeParmBoundIntersection");
            assertHasTypeAnnots(t.getTypeParameters().get(0), emptyList());
            assertHasAnnots(t.getTypeParameters().get(0).getSymbol(), ANNOT_A);
            assertHasTypeAnnots(t.getTypeParameters().get(1), emptyList());
            assertHasTypeAnnots(t.getFormalParameters().get(0), emptyList());
            assertHasAnnots(t.getFormalParameters().get(0).getSymbol(), ANNOT_A);

            JIntersectionType ub = (JIntersectionType) t.getTypeParameters().get(1).getUpperBound();
            assertHasTypeAnnots(ub, emptyList());
            assertHasTypeAnnots(ub.getComponents().get(0), ANNOT_B);
            assertHasTypeAnnots(ub.getComponents().get(1), ANNOT_A);
        }
    }

    @ParameterizedTest
    @EnumSource
    void testTypeAnnotOnReceiver(SymImplementation impl) {
        JClassType sym = impl.getDeclaration(ClassWithTypeAnnotationsOnMethods.class);

        /*
            abstract void abOnReceiver(@A @B ClassWithTypeAnnotationsOnMethods this);
         */

        {
            JMethodSig t = getMethodType(sym, "abOnReceiver");
            assertThat(t.getFormalParameters(), Matchers.empty());
            assertHasTypeAnnots(t.getAnnotatedReceiverType(), ANNOTS_A_B);
        }
    }


}
