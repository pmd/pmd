/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.lang.reflect.Modifier;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowsList;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * @author Romain Pelisse, bugfix for [ 1522517 ] False +:
 *         UselessOverridingMethod
 */
public class UselessOverridingMethodRule extends AbstractJavaRule {
    private static final String CLONE_METHOD_NAME = "clone";

    // TODO extend AbstractIgnoredAnnotationRule node
    // TODO ignore if there is javadoc
    private static final PropertyDescriptor<Boolean> IGNORE_ANNOTATIONS_DESCRIPTOR =
            booleanProperty("ignoreAnnotations")
            .defaultValue(false)
            .desc("Ignore annotations")
            .build();

    private String packageName;

    public UselessOverridingMethodRule() {
        definePropertyDescriptor(IGNORE_ANNOTATIONS_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        packageName = "";
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration clz, Object data) {
        if (clz.isInterface()) {
            return data;
        }
        return super.visit(clz, data);
    }

    // TODO: this method should be externalize into an utility class, shouldn't it ?
    private boolean isMethodResultType(ASTMethodDeclaration node, Class<?> resultType) {
        ASTType type = node.getResultTypeNode();
        return TypeTestUtil.isA(resultType, type);
    }

    // TODO: this method should be externalize into an utility class, shouldn't it ?
    private boolean isMethodThrowingType(ASTMethodDeclaration node, Class<? extends Exception> exceptionType) {
        @Nullable ASTThrowsList thrownExceptions = node.getThrowsList();
        if (thrownExceptions != null) {
            for (ASTClassOrInterfaceType type : thrownExceptions) {
                if (TypeTestUtil.isA(exceptionType, type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        packageName = node.getName();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // Can skip abstract methods and methods whose only purpose is to
        // guarantee that the inherited method is not changed by finalizing
        // them.
        if (node.getModifiers().hasAny(JModifier.ABSTRACT, JModifier.FINAL, JModifier.NATIVE, JModifier.SYNCHRONIZED)) {
            return super.visit(node, data);
        }
        // We can also skip the 'clone' method as they are generally
        // 'useless' but as it is considered a 'good practice' to
        // implement them anyway ( see bug 1522517)
        if (isCloneMethod(node)) {
            return super.visit(node, data);
        }

        ASTBlock block = node.getBody();
        if (block == null) {
            return super.visit(node, data);
        }
        // Only process functions with one BlockStatement
        if (block.getNumChildren() != 1 || block.descendants(ASTStatement.class).count() != 1) {
            return super.visit(node, data);
        }

        Node statement = block.getChild(0);
        if (statement.getNumChildren() == 0) {
            return super.visit(node, data); // skips empty return statements
        }

        if (!getProperty(IGNORE_ANNOTATIONS_DESCRIPTOR)
                && node.getDeclaredAnnotations().any(it -> !TypeTestUtil.isA(Override.class, it))) {
            return super.visit(node, data);
        }

        // merely calling super.foo() or returning super.foo()
        if ((statement instanceof ASTExpressionStatement || statement instanceof ASTReturnStatement)
                && statement.getNumChildren() == 1
                && statement.getChild(0) instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) statement.getChild(0);
            if (!isSuperCallSameMethod(node, methodCall)) {
                return super.visit(node, data);
            }
        } else {
            return super.visit(node, data);
        }

        if (modifiersChanged(node)) {
            return super.visit(node, data);
        }

        // All arguments are passed through directly or there were no arguments
        addViolation(data, node);

        return super.visit(node, data);
    }

    private boolean isSuperCallSameMethod(ASTMethodDeclaration node, ASTMethodCall methodCall) {
        if (!(methodCall.getQualifier() instanceof ASTSuperExpression)) {
            return false;
        }

        @NonNull
        ASTFormalParameters formalParameters = node.getFormalParameters();
        @NonNull
        ASTArgumentList arguments = methodCall.getArguments();

        if (node.getName().equals(methodCall.getMethodName())
                && formalParameters.size() == arguments.size()) {
            // simple case - no args
            if (formalParameters.size() == 0) {
                return true;
            }

            // compare each arg
            for (int i = 0; i < node.getArity(); i++) {
                ASTFormalParameter formalParam = formalParameters.get(i);
                ASTExpression arg = arguments.get(i);
                
                if (!(arg instanceof ASTVariableAccess)) {
                    return false;
                }
                ASTVariableAccess varAccess = (ASTVariableAccess) arg;
                if (!formalParam.getVarId().getName().equals(varAccess.getName())) {
                    return false;
                }
            }
            
            // now all args matched
            return true;
        }
        return false;
    }

    private boolean isCloneMethod(ASTMethodDeclaration node) {
        boolean isCloneAndPublic = CLONE_METHOD_NAME.equals(node.getName()) && node.getVisibility() == Visibility.V_PUBLIC;
        boolean hasNoParameters = node.getArity() == 0;
        return isCloneAndPublic
                && hasNoParameters
                && this.isMethodResultType(node, Object.class)
                && this.isMethodThrowingType(node, CloneNotSupportedException.class);
    }

    private boolean modifiersChanged(ASTMethodDeclaration node) {
        JClassType type = node.getEnclosingType().getTypeMirror();

        // search method with same name up the hierarchy
        JClassType superType = type.getSuperClass();
        JMethodSig declaredMethod = null;
        while (superType != null && declaredMethod == null) {
            List<JMethodSymbol> superMethods = superType.getSymbol().getDeclaredMethods();
            for (JMethodSymbol m : superMethods) {
                if (TypeOps.overrides(node.getTypeSystem().sigOf(node.getSymbol()), m.getTypeSystem().sigOf(m), type)) {
                    declaredMethod = superType.getDeclaredMethod(m);
                    break;
                }
            }
            superType = superType.getSuperClass();
        }
        return declaredMethod != null && isElevatingAccessModifier(node, declaredMethod);
    }

    private boolean isElevatingAccessModifier(ASTMethodDeclaration overridingMethod, JMethodSig superMethod) {
        String superPackageName = superMethod.getDeclaringType().getSymbol().getPackageName();

        // Note: can't simply compare superMethod.getModifiers() with overridingMethod.getModifiers()
        // since AccessNode#PROTECTED != Modifier#PROTECTED.
        boolean elevatingFromProtected = Modifier.isProtected(superMethod.getModifiers())
                && overridingMethod.getVisibility() != Visibility.V_PROTECTED;
        boolean elevatingFromPackagePrivate = superMethod.getModifiers() == 0
            && !overridingMethod.getModifiers().getExplicitModifiers().isEmpty();
        boolean elevatingIntoDifferentPackage = !packageName.equals(superPackageName)
                && !Modifier.isPublic(superMethod.getModifiers());

        return elevatingFromProtected
                || elevatingFromPackagePrivate
                || elevatingIntoDifferentPackage;
    }

}
