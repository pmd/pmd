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
