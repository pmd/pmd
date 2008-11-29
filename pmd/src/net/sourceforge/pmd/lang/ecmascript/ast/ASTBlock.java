/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Block;

public class ASTBlock extends AbstractEcmascriptNode<Block> {
    public ASTBlock(Block block) {
	super(block);
    }
}
