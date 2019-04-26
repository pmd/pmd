/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * This interface can be used to tag the root node of various ASTs.
 */
public interface RootNode extends Node {
    // that's only a marker interface.
    // TODO we could add some utilities here eg to get the file name,
    //  the language of the node,
    //  the source code of the file (as recently done in PLSQL - #1728),
    //  the whole token chain, etc
}
