/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PRIVATE;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class ClassWithOnlyPrivateConstructorsShouldBeFinalRule extends AbstractJavaRulechainRule {

    public ClassWithOnlyPrivateConstructorsShouldBeFinalRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isRegularClass()
            && !node.hasModifiers(JModifier.FINAL)
            && hasOnlyPrivateCtors(node)
            && isNotInstantiated(node)
            && hasNoSubclasses(node)) {
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
        return sub != superClass && TypeTestUtil.isA(superClass.getTypeMirror(), sub);
    }

    private boolean hasOnlyPrivateCtors(ASTClassOrInterfaceDeclaration node) {
        return node.getDeclarations(ASTConstructorDeclaration.class).all(it -> it.getVisibility() == V_PRIVATE)
            && (node.getVisibility() == V_PRIVATE // then the default ctor is private
            || node.getDeclarations(ASTConstructorDeclaration.class).nonEmpty());
    }

    private boolean isNotInstantiated(ASTClassOrInterfaceDeclaration node) {
        List<JExecutableSymbol> constructorCalls = node.getRoot().descendants(ASTConstructorCall.class)
                .toStream()
                .map(call -> call.getMethodType().getSymbol())
                .collect(Collectors.toList());

        List<ASTConstructorDeclaration> constructors = node.descendants(ASTConstructorDeclaration.class)
                .toList();

        for (JExecutableSymbol call : constructorCalls) {
            if (
                // an explicitly defined constructor is used
                constructors.contains(call.tryGetNode())

                // or the implicitly defined default ctor is used
                || node.isNested() && node.getVisibility() == Visibility.V_PRIVATE
                        && isDefaultConstructorCall(node, call)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDefaultConstructorCall(ASTClassOrInterfaceDeclaration node, JExecutableSymbol constructorCall) {
        return constructorCall instanceof JConstructorSymbol
                && constructorCall.getArity() == 0
                && constructorCall.getEnclosingClass().tryGetNode() == node;
    }
}
