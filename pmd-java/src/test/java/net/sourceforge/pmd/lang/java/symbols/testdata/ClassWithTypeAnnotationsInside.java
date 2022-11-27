/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.TypeAnnotReflectionTest;

/**
 * See {@link TypeAnnotReflectionTest}.
 *
 * @author Clément Fournier
 */
public class ClassWithTypeAnnotationsInside {


    @TypeUseAnnot int intField;

    @TypeUseAnnot List<String> annotOnList;
    List<@TypeUseAnnot String> annotOnListArg;
    @TypeUseAnnot List<@TypeUseAnnot String> annotOnBothListAndArg;

    @TypeUseAnnot int[] annotOnArrayComponent;
    int @TypeUseAnnot [] annotOnArrayDimension;
    // this annotates the int[]
    int[] @TypeUseAnnot @SecondTypeUseAnnot [] twoAnnotsOnOuterArrayDim;
    int @TypeUseAnnot [][] annotOnInnerArrayDim;
    int @TypeUseAnnot(1) [] @TypeUseAnnot(2) [] annotsOnBothArrayDims;


    ClassWithTypeAnnotationsInside.@TypeUseAnnot Inner1 inner1WithAnnot;
    @TypeUseAnnot ClassWithTypeAnnotationsInside.@TypeUseAnnot Inner1 inner1WithAnnotOnOuterToo;
    @TypeUseAnnot ClassWithTypeAnnotationsInside.@TypeUseAnnot Inner1.Inner2 inner2WithAnnotOnBothOuter;
    @TypeUseAnnot ClassWithTypeAnnotationsInside.@TypeUseAnnot @SecondTypeUseAnnot Inner1.@TypeUseAnnot Inner2 inner2WithAnnotOnAll;
    ClassWithTypeAnnotationsInside.@TypeUseAnnot @SecondTypeUseAnnot Inner1.@TypeUseAnnot Inner2 inner2WithAnnotOnAllExceptOuter;


    OuterG<A, A>.@TypeUseAnnot Inner5 annotOnInnerWithOuterGeneric;
    OuterG<@TypeUseAnnot A, A>.@TypeUseAnnot Inner5 annotOnOuterGenericArg;
    OuterG<A, @TypeUseAnnot A>.@TypeUseAnnot Inner5 annotOnOuterGenericArg2;
    @TypeUseAnnot OuterG<A, @TypeUseAnnot A>.Inner5 annotOnOuterGenericArgAndOuter;
    @TypeUseAnnot OuterG<A, @TypeUseAnnot A>.@TypeUseAnnot InnerG<@TypeUseAnnot A> annotOnOuterGenericArgAndInner;


    private static class A { }

    class Inner1 {

        class Inner2 { }
    }

    static class OuterG<A, B> {

        class Inner5 { }
        class InnerG<X> { }
    }
}
