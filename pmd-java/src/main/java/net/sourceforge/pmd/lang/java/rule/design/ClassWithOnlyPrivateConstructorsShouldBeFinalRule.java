/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PRIVATE;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class ClassWithOnlyPrivateConstructorsShouldBeFinalRule extends AbstractJavaRulechainRule {

    private static final String LOMBOK_VALUE = "lombok.Value";
    public static final String LOMBOK_NO_ARGS_CONSTRUCTOR = "lombok.NoArgsConstructor";
    public static final String LOMBOK_REQUIRED_ARGS_CONSTRUCTOR = "lombok.RequiredArgsConstructor";
    public static final String LOMBOK_ALL_ARGS_CONSTRUCTOR = "lombok.AllArgsConstructor";
    public static final String LOMBOK_PRIVATE_ACCESS = "PRIVATE";

    public ClassWithOnlyPrivateConstructorsShouldBeFinalRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isRegularClass()
            && !node.hasModifiers(JModifier.FINAL)
            && !node.isAnnotationPresent(LOMBOK_VALUE)
            && !hasPublicLombokConstructors(node)
            && hasOnlyPrivateCtors(node)
            && hasNoSubclasses(node)) {
            asCtx(data).addViolation(node);
        }
        return null;
    }

    private boolean hasPublicLombokConstructors(ASTClassOrInterfaceDeclaration node) {
        List<ASTAnnotation> annotations = node.getDeclaredAnnotations()
                .filter(t -> TypeTestUtil.isA(LOMBOK_NO_ARGS_CONSTRUCTOR, t)
                        || TypeTestUtil.isA(LOMBOK_REQUIRED_ARGS_CONSTRUCTOR, t)
                        || TypeTestUtil.isA(LOMBOK_ALL_ARGS_CONSTRUCTOR, t))
                .toList();
        return !annotations.isEmpty()
                && annotations.stream().noneMatch(this::hasPrivateAccessModifierOption);
    }

    private boolean hasPrivateAccessModifierOption(ASTAnnotation annotation) {
        return annotation.getValuesForName("access")
                .filter(v -> v instanceof ASTFieldAccess)
                .map(v -> ASTFieldAccess.class.cast(v))
                .toStream()
                .map(ASTFieldAccess::getName)
                .anyMatch(name -> LOMBOK_PRIVATE_ACCESS.equals(name));
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
