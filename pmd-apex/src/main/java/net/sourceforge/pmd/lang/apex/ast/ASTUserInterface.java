package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.data.ast.Identifier;
import apex.jorje.semantic.ast.compilation.UserInterface;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTUserInterface extends AbstractApexNode<UserInterface> implements RootNode {

	public ASTUserInterface(UserInterface userInterface) {
		super(userInterface);
	}

	@Override
	public String getImage() {
		try {
			Field field = node.getClass().getDeclaredField("name");
			field.setAccessible(true);
			Identifier name = (Identifier) field.get(node);
			return name.value;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return super.getImage();
	}
}
