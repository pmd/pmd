/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

/**
 * A base visitor that by default does a top-down visit of the tree.
 *
 * @param <P> Parameter of the visit
 * @param <R> Return type of the visit
 */
public abstract class EcmascriptVisitorBase<P, R> extends AstVisitorBase<P, R> implements EcmascriptVisitor<P, R> {

}
