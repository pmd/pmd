/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrErrorNode;

public final class KotlinErrorNode extends BaseAntlrErrorNode<KotlinNode> implements KotlinNode {

    KotlinErrorNode(Token token) {
        super(token);
    }

}
