/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.StringUtil;

/**
 * This rule detects private methods, that are not used and can therefore be
 * deleted.
 */
public class UnusedPrivateMethodRule extends AbstractIgnoredAnnotationRule {
    private static final Set<String> SERIALIZATION_METHODS = new HashSet<>(Arrays.asList(
            "readObject", "writeObject", "readResolve", "writeReplace"));

    public UnusedPrivateMethodRule() {
        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return Collections.singletonList("java.lang.Deprecated");
    }

    /**
     * Return a set of method names which are considered used. Only the
     * no-arg overload is considered used.
     */
    private static Set<String> methodsUsedByAnnotations(ASTClassOrInterfaceDeclaration klassDecl) {
        Set<String> result = Collections.emptySet();
        for (ASTAnyTypeBodyDeclaration declaration : klassDecl.getDeclarations()) {
            for (ASTAnnotation annot : declaration.findChildrenOfType(ASTAnnotation.class)) {
                if (TypeTestUtil.isA("org.junit.jupiter.params.provider.MethodSource", annot)) {
                    // MethodSource#value() -> String[], there may be several of those methods
                    // todo this is not robust, revisit in pmd 7
                    for (ASTLiteral literal : annot.findDescendantsOfType(ASTLiteral.class)) {
                        if (literal.isStringLiteral()) {
                            if (result.isEmpty()) {
                                result = new HashSet<>(); // make writable
                            }
                            result.add(StringUtil.removeDoubleQuotes(literal.getImage()));
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        Set<String> methodsUsedByAnnotations = methodsUsedByAnnotations(node);

        Map<MethodNameDeclaration, List<NameOccurrence>> methods = node.getScope().getEnclosingScope(ClassScope.class)
                                                                       .getMethodDeclarations();
        for (MethodNameDeclaration mnd : findUnique(methods)) {
            List<NameOccurrence> occs = methods.get(mnd);
            if (!privateAndNotExcluded(mnd)
                || hasIgnoredAnnotation((Annotatable) mnd.getNode().getParent())
                || mnd.getParameterCount() == 0 && methodsUsedByAnnotations.contains(mnd.getName())) {
                continue;
            }
            if (occs.isEmpty()) {
                addViolation(data, mnd.getNode(), mnd.getImage() + mnd.getParameterDisplaySignature());
            } else {
                if (isMethodNotCalledFromOtherMethods(mnd, occs)) {
                    addViolation(data, mnd.getNode(), mnd.getImage() + mnd.getParameterDisplaySignature());
                }

            }
        }
        return data;
    }

    private Set<MethodNameDeclaration> findUnique(Map<MethodNameDeclaration, List<NameOccurrence>> methods) {
        // some rather hideous hackery here
        // to work around the fact that PMD does not yet do full type analysis
        // when it does, delete this
        Set<MethodNameDeclaration> unique = new HashSet<>();
        Set<String> sigs = new HashSet<>();
        for (MethodNameDeclaration mnd : methods.keySet()) {
            String sig = mnd.getImage() + mnd.getParameterCount() + mnd.isVarargs();
            if (!sigs.contains(sig)) {
                unique.add(mnd);
            }
            sigs.add(sig);
        }
        return unique;
    }

    /**
     * Checks, whether the given method {@code mnd} is called from other methods or constructors.
     *
     * @param mnd the private method, that is checked
     * @param occs the usages of the private method
     * @return <code>true</code> if the method is not used (except maybe from itself), <code>false</code>
     *         if the method is called by other methods.
     */
    private boolean isMethodNotCalledFromOtherMethods(MethodNameDeclaration mnd, List<NameOccurrence> occs) {
        int callsFromOutsideMethod = 0;
        for (NameOccurrence occ : occs) {
            Node occNode = occ.getLocation();
            ASTConstructorDeclaration enclosingConstructor = occNode
                    .getFirstParentOfType(ASTConstructorDeclaration.class);
            if (enclosingConstructor != null) {
                callsFromOutsideMethod++;
                break; // Do we miss unused private constructors here?
            }
            ASTInitializer enclosingInitializer = occNode.getFirstParentOfType(ASTInitializer.class);
            if (enclosingInitializer != null) {
                callsFromOutsideMethod++;
                break;
            }

            ASTMethodDeclaration enclosingMethod = occNode.getFirstParentOfType(ASTMethodDeclaration.class);
            if (enclosingMethod == null || !mnd.getNode().getParent().equals(enclosingMethod)) {
                callsFromOutsideMethod++;
                break;
            }
        }
        return callsFromOutsideMethod == 0;
    }

    private boolean privateAndNotExcluded(MethodNameDeclaration mnd) {
        ASTMethodDeclaration node = mnd.getMethodNameDeclaratorNode().getParent();
        return node.isPrivate() && !SERIALIZATION_METHODS.contains(node.getName());
    }
}
