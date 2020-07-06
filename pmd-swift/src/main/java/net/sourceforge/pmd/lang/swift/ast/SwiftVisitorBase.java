/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

/**
 * Base class for swift visitors.
 */
public abstract class SwiftVisitorBase<P, R> extends AstVisitorBase<P, R> implements SwiftVisitor<P, R> {

}
