/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.impls;


import net.sourceforge.pmd.lang.ast.Node;

public class WithSuperClass<T extends Node> extends GenericClass<T, T> {


    @Override
    public <U, O> void anOverload(int bb) {

    }


    public <U, O> void anOverload(int bb, String bachir, int other) {

    }

}
