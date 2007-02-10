package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTTypeParameters;
import net.sourceforge.pmd.ast.Node;

public abstract class AbstractJUnitRule extends AbstractRule {
    
    public boolean isJUnitMethod(ASTMethodDeclaration method, Object data) {
        if (!method.isPublic() || method.isAbstract() || method.isNative() || method.isStatic()) {
            return false; // skip various inapplicable method variations
        }

        Node node = method.jjtGetChild(0);
        if (node instanceof ASTTypeParameters) {
            node = method.jjtGetChild(1);
        }
        return ((ASTResultType) node).isVoid() && method.getMethodName().startsWith("test");
    }
    
    public boolean isJUnit4Method(){
        return false;
    }
}
