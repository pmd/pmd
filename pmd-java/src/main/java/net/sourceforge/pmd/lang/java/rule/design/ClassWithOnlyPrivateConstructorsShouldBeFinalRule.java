/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PRIVATE;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class ClassWithOnlyPrivateConstructorsShouldBeFinalRule extends AbstractJavaRulechainRule {

    public ClassWithOnlyPrivateConstructorsShouldBeFinalRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isRegularClass()
            && !node.hasModifiers(JModifier.FINAL)
            && !node.isAnnotationPresent("lombok.Value")
            && hasOnlyPrivateCtors(node)
            && hasNoSubclasses(node)
            && !node.isAnnotationPresent("lombok.Value")) {
            addViolation(data, node);
        }
        return null;
    }

    private boolean hasNoSubclasses(ASTClassOrInterfaceDeclaration klass) {
        return klass.getRoot()
                    .descendants(ASTAnyTypeDeclaration.class)
                    .crossFindBoundaries()
                    .none(it -> doesExtend(it, klass));
    }

    private boolean doesExtend(ASTAnyTypeDeclaration sub, ASTClassOrInterfaceDeclaration superClass) {
        return sub != superClass && TypeTestUtil.isA(superClass.getTypeMirror().getErasure(), sub);
    }

    private boolean hasOnlyPrivateCtors(ASTClassOrInterfaceDeclaration node) {
        return node.getDeclarations(ASTConstructorDeclaration.class).all(it -> it.getVisibility() == V_PRIVATE)
            && (node.getVisibility() == V_PRIVATE // then the default ctor is private
            || node.getDeclarations(ASTConstructorDeclaration.class).nonEmpty());
    }

}
