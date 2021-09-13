/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

/**
 * Base class for kotlin visitors.
 */
public abstract class KotlinVisitorBase<P, R> extends AstVisitorBase<P, R> implements KotlinVisitor<P, R> {

}
