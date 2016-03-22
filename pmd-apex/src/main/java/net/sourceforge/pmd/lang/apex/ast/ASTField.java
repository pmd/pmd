package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Field;

public class ASTField extends AbstractApexNode<Field> {

	public ASTField(Field field) {
		super(field);
	}

	@Override
	public String getImage() {
		return node.getFieldInfo().getName();
	}
}