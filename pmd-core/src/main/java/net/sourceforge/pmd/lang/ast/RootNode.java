/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * This interface identifies the root node of an AST. Each language
 * implementation must ensure that every AST its parser produces has
 * a RootNode as its root, and that there is no other RootNode instance
 * in the tree.
 */
public interface RootNode extends Node {


    @Override
    AstInfo<? extends RootNode> getAstInfo();

}
