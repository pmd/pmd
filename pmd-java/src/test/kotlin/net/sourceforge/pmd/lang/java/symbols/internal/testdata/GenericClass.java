/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.testdata;


import net.sourceforge.pmd.lang.ast.Node;


public class GenericClass<T, F extends Node> {


    public void anOverload(int bb) {

    }


    public void anOverload(int bb, String bachir) {

    }

}
