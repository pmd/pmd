package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Field;

public class ASTField extends AbstractApexNode<Field> {

    public ASTField(Field field) {
        super(field);
    }

    /**
     * Accept the visitor. Note: This needs to be in each concrete node class,
     * as otherwise the visitor won't work - as java resolves the type "this" at
     * compile time.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getFieldInfo().getName();
    }
}