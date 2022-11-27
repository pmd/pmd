/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

import java.util.List;

/**
 * @author Clément Fournier
 */
public class ClassWithTypeAnnotationsInside {


    @TypeUseAnnot int intField;

    @TypeUseAnnot List<String> annotOnList;
    List<@TypeUseAnnot String> annotOnListArg;
    @TypeUseAnnot List<@TypeUseAnnot String> annotOnBothListAndArg;

    @TypeUseAnnot int[] annotOnArrayComponent;
    int @TypeUseAnnot [] annotOnArrayDimension;
    int[] @TypeUseAnnot [] annotOnOuterArrayDim;
    int @TypeUseAnnot [][] annotOnInnerArrayDim;
    int @TypeUseAnnot(1) [] @TypeUseAnnot(2) [] annotsOnBothArrayDims;
}
