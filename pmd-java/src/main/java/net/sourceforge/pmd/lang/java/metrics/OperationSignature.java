/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 * @author Clément Fournier
 *
 */
public class OperationSignature extends Signature {

    public final Role    role;
    public final boolean isAbstract;
    
    public static enum Role {
        GETTER_OR_SETTER, CONSTRUCTOR, METHOD, STATIC;

        public static final Role[] ALL = new Role[] { GETTER_OR_SETTER, CONSTRUCTOR, METHOD, STATIC };
    }
    
    public OperationSignature(Visibility visibility, Role role, boolean isAbstract) {
        super(visibility);
        this.role = role;
        this.isAbstract = isAbstract;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof OperationSignature) {
            // TODO
            return true;
        }
        return false;
    }
    
    public static OperationSignature buildFor(ASTMethodDeclaration node) {
        // TODO better getter or setter detection
        boolean isGetterOrSetter = node.getName().startsWith("get") || node.getName().startsWith("set");
        Role role = isGetterOrSetter ? Role.GETTER_OR_SETTER : node.isStatic() ? Role.STATIC : Role.METHOD;

        return new OperationSignature(getVisibility(node), role, node.isAbstract());
    }
    
    public static OperationSignature buildFor(ASTConstructorDeclaration node) {
        return new OperationSignature(getVisibility(node), Role.CONSTRUCTOR, node.isAbstract());
    }
    
}