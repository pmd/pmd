/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

/**
 * @since 6.3.0
 */
@SuppressWarnings("unused")
public class ArrayVariableDeclaration {

    /* In the following declarations:
     * * The VariableDeclarators of a should have type int[], b should be int[][], c should be String[], d should be String[][]
     * * The types of the FieldDeclaration and LocalVariableDeclaration nodes are undefined
     *
     * See https://github.com/pmd/pmd/issues/910
     */

    /*
     FieldDeclaration
     |+ Type
     | + ReferenceType[@Array=true() and @ArrayDims=1]
     |   + PrimitiveType
     |
     |+ VariableDeclarator
     | + VariableDeclaratorId[@Image="a" and @Array=false() and ArrayDims=0 and typeIs(.)='int[].class']
     |
     |+ VariableDeclarator
     | + VariableDeclaratorId[@Image="b" and @Array=true() and ArrayDims=1 and typeIs(.)='int[][].class']
    */
    public int[] a, b[];    // SUPPRESS CHECKSTYLE now
    public String[] c, d[]; // SUPPRESS CHECKSTYLE now


    public void testLocalVars() {
        int[] a, b[];    // SUPPRESS CHECKSTYLE now
        String[] c, d[]; // SUPPRESS CHECKSTYLE now
    }
}
