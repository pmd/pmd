/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility.V_PRIVATE;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class ClassWithOnlyPrivateConstructorsShouldBeFinalRule extends AbstractJavaRulechainRule {

    public ClassWithOnlyPrivateConstructorsShouldBeFinalRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        if (node.isRegularClass()
            && !node.hasModifiers(JModifier.FINAL)
            && !node.isAnnotationPresent("lombok.Value")
            && !hasPublicLombokConstructors(node)
            && hasOnlyPrivateCtors(node)
            && hasNoSubclasses(node)) {
            asCtx(data).addViolation(node);
        }
        return null;
    }

    private boolean hasPublicLombokConstructors(ASTClassDeclaration node) {
        return node.getDeclaredAnnotations()
                   .filter(it -> TypeTestUtil.isA("lombok.NoArgsConstructor", it)
                       || TypeTestUtil.isA("lombok.RequiredArgsConstructor", it)
                       || TypeTestUtil.isA("lombok.AllArgsConstructor", it))
                   .any(it -> it.getFlatValue("access").filterIs(ASTNamedReferenceExpr.class).none(ref -> "PRIVATE".equals(ref.getName())));
    }

    private boolean hasNoSubclasses(ASTClassDeclaration klass) {
        return klass.getRoot()
                    .descendants(ASTTypeDeclaration.class)
                    .crossFindBoundaries()
                    .none(it -> doesExtend(it, klass));
    }

    private boolean doesExtend(ASTTypeDeclaration sub, ASTClassDeclaration superClass) {
        return sub != superClass && TypeTestUtil.isA(superClass.getTypeMirror().getErasure(), sub);
    }

    private boolean hasOnlyPrivateCtors(ASTClassDeclaration node) {
        return node.getDeclarations(ASTConstructorDeclaration.class).all(it -> it.getVisibility() == V_PRIVATE)
            && (node.getVisibility() == V_PRIVATE // then the default ctor is private
            || node.getDeclarations(ASTConstructorDeclaration.class).nonEmpty());
    }

}
