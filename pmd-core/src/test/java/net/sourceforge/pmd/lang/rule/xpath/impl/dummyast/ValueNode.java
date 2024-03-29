/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl.dummyast;

import net.sourceforge.pmd.lang.document.Chars;

/**
 * This interface is implemented by both AbstractNode and ConcreteNode.
 * <p>This is similar to the case in pmd-java, where ASTLiteral is implemented
 * by both ASTStringLiteral (and others) and AbstractLiteral.</p>
 */
public interface ValueNode {
    Chars getValue();
}
