/*
 * User: tom
 * Date: Nov 6, 2002
 * Time: 8:04:20 AM
 */
package net.sourceforge.pmd.ast;

public class ASTCatch {
    private ASTFormalParameter parameter;
    private ASTBlock block;

    public ASTCatch(ASTFormalParameter parameter, ASTBlock block) {
        this.parameter = parameter;
        this.block = block;
    }

    public ASTFormalParameter getFormalParameter() {
        return parameter;
    }

    public ASTBlock getBlock() {
        return block;
    }
}
