/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;

/**
 * See {@link net.sourceforge.pmd.lang.java.symbols.internal.TypeAnnotReflectionTest}.
 *
 * @author Clément Fournier
 */
@SuppressWarnings("all")
public class ClassWithTypeAnnotationsInside {


    @A int intField;

    @A List<String> annotOnList;
    List<@A String> annotOnListArg;
    @A List<@A String> annotOnBothListAndArg;

    @A int[] annotOnArrayComponent;
    int @A [] annotOnArrayDimension;
    // this annotates the int[]
    int[] @A @B [] twoAnnotsOnOuterArrayDim;
    int @A [][] annotOnInnerArrayDim;
    int @A(1) [] @A(2) [] annotsOnBothArrayDims;


    Outer.@A Inner1 inner1WithAnnot;
    @A Outer.@B Inner1 inner1WithAnnotOnOuterToo;
    @A Outer.@B Inner1.Inner2 inner2WithAnnotOnBothOuter;
    @A Outer.@A @B Inner1.@B Inner2 inner2WithAnnotOnAll;
    Outer.@A @B Inner1.@A Inner2 inner2WithAnnotOnAllExceptOuter;


    OuterG<T, T>.@A Inner5 annotOnInnerWithOuterGeneric;
    OuterG<@A T, T>.@A Inner5 annotOnOuterGenericArg;
    OuterG<T, @A T>.@A Inner5 annotOnOuterGenericArg2;
    @A OuterG<T, @A T>.Inner5 annotOnOuterGenericArgAndOuter;
    @A OuterG<T, @A T>.@A InnerG<@A T> annotOnOuterGenericArgAndInner;


    OuterG<@A ? extends @B String, ? super @A @B T> severalWildcards;
    @A OuterG<@A @B ? extends @B String, @A List<@A @B Object>> complicatedField;


    @Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
    public @interface A {

        int value() default 1;
    }

    @Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
    public @interface B { }


    private static class T { }

    static class Outer {

        class Inner1 {

            class Inner2 { }
        }

    }

    static class OuterG<A, B> {

        class Inner5 { }

        class InnerG<X> { }
    }
}
