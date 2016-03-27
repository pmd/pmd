package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.data.ast.Identifier;
import apex.jorje.semantic.ast.compilation.UserInterface;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTUserInterface extends AbstractApexNode<UserInterface> implements RootNode {

	public ASTUserInterface(UserInterface userInterface) {
		super(userInterface);
	}

    /**
     * Accept the visitor.
     * Note: This needs to be in each concrete node class, as otherwise
     * the visitor won't work - as java resolves the type "this" at compile
     * time.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
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
