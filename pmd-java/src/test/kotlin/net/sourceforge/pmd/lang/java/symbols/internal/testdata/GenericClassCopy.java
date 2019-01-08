/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.testdata;


import net.sourceforge.pmd.lang.ast.Node;


public class GenericClassCopy<T, F extends Node> {


    public <U, O> void anOverload(int bb) {

    }


    public <U, O> void anOverload(int bb, String bachir) {

    }

}
