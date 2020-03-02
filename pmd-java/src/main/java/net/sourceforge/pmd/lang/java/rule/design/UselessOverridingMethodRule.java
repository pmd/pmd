/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
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
        ASTResultType type = node.getResultType();
        if (type != null && type.getChild(0) instanceof ASTType) {
            Class<?> resolvedResultType = ((ASTType) type.getChild(0)).getType();
            return resultType.equals(resolvedResultType);
        }
        return false;
    }

    // TODO: this method should be externalize into an utility class, shouldn't it ?
    private boolean isMethodThrowingType(ASTMethodDeclaration node, Class<? extends Exception> exceptionType) {
        ASTNameList thrownsExceptions = node.getThrows();
        if (thrownsExceptions != null) {
            List<ASTName> names = thrownsExceptions.findChildrenOfType(ASTName.class);
            for (ASTName name : names) {
                if (name.getType() != null && name.getType() == exceptionType) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        packageName = node.getPackageNameImage();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // Can skip abstract methods and methods whose only purpose is to
        // guarantee that the inherited method is not changed by finalizing
        // them.
        if (node.isAbstract() || node.isFinal() || node.isNative() || node.isSynchronized()) {
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
        if (block.getNumChildren() != 1 || block.findDescendantsOfType(ASTStatement.class).size() != 1) {
            return super.visit(node, data);
        }

        Node statement = block.getChild(0).getChild(0);
        if (statement.getChild(0).getNumChildren() == 0) {
            return data; // skips empty return statements
        }
        Node statementGrandChild = statement.getChild(0).getChild(0);
        ASTPrimaryExpression primaryExpression;

        if (statementGrandChild instanceof ASTPrimaryExpression) {
            primaryExpression = (ASTPrimaryExpression) statementGrandChild;
        } else {
            List<ASTPrimaryExpression> primaryExpressions = statementGrandChild
                    .findChildrenOfType(ASTPrimaryExpression.class);
            if (primaryExpressions.size() != 1) {
                return super.visit(node, data);
            }
            primaryExpression = primaryExpressions.get(0);
        }

        ASTPrimaryPrefix primaryPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (!primaryPrefix.usesSuperModifier()) {
            return super.visit(node, data);
        }

        List<ASTPrimarySuffix> primarySuffixList = primaryExpression.findChildrenOfType(ASTPrimarySuffix.class);
        if (primarySuffixList.size() != 2) {
            // extra method call on result of super method
            return super.visit(node, data);
        }

        ASTPrimarySuffix primarySuffix = primarySuffixList.get(0);
        if (!primarySuffix.hasImageEqualTo(node.getName())) {
            return super.visit(node, data);
        }
        // Process arguments
        primarySuffix = primarySuffixList.get(1);
        ASTArguments arguments = (ASTArguments) primarySuffix.getChild(0);
        ASTFormalParameters formalParameters = node.getFormalParameters();
        if (formalParameters.getNumChildren() != arguments.getNumChildren()) {
            return super.visit(node, data);
        }

        if (!getProperty(IGNORE_ANNOTATIONS_DESCRIPTOR)) {
            ASTClassOrInterfaceBodyDeclaration parent = (ASTClassOrInterfaceBodyDeclaration) node.getParent();
            for (int i = 0; i < parent.getNumChildren(); i++) {
                Node n = parent.getChild(i);
                if (n instanceof ASTAnnotation) {
                    if (n.getChild(0) instanceof ASTMarkerAnnotation) {
                        // @Override is ignored
                        if ("Override".equals(((ASTName) n.getChild(0).getChild(0)).getImage())) {
                            continue;
                        }
                    }
                    return super.visit(node, data);
                }
            }
        }

        // different number of args
        if (arguments.size() != node.getArity()) {
            return super.visit(node, data);
        }

        if (arguments.size() > 0) {
            ASTArgumentList argumentList = (ASTArgumentList) arguments.getChild(0);
            for (int i = 0; i < argumentList.getNumChildren(); i++) {
                Node expressionChild = argumentList.getChild(i).getChild(0);
                if (!(expressionChild instanceof ASTPrimaryExpression) || expressionChild.getNumChildren() != 1) {
                    // The arguments are not simply passed through
                    return super.visit(node, data);
                }

                ASTPrimaryExpression argumentPrimaryExpression = (ASTPrimaryExpression) expressionChild;
                ASTPrimaryPrefix argumentPrimaryPrefix = (ASTPrimaryPrefix) argumentPrimaryExpression.getChild(0);
                if (argumentPrimaryPrefix.getNumChildren() == 0) {
                    // The arguments are not simply passed through (using "this" for instance)
                    return super.visit(node, data);
                }
                Node argumentPrimaryPrefixChild = argumentPrimaryPrefix.getChild(0);
                if (!(argumentPrimaryPrefixChild instanceof ASTName)) {
                    // The arguments are not simply passed through
                    return super.visit(node, data);
                }

                ASTName argumentName = (ASTName) argumentPrimaryPrefixChild;
                ASTFormalParameter formalParameter = (ASTFormalParameter) formalParameters.getChild(i);
                ASTVariableDeclaratorId variableId = formalParameter.getFirstChildOfType(ASTVariableDeclaratorId.class);
                if (!argumentName.hasImageEqualTo(variableId.getImage())) {
                    // The arguments are not simply passed through
                    return super.visit(node, data);
                }
            }
        }

        if (modifiersChanged(node)) {
            return super.visit(node, data);
        }

        // All arguments are passed through directly or there were no arguments
        addViolation(data, node, getMessage());

        return super.visit(node, data);
    }

    private boolean isCloneMethod(ASTMethodDeclaration node) {
        boolean isCloneAndPublic = CLONE_METHOD_NAME.equals(node.getName()) && node.isPublic();
        boolean hasNoParameters = node.getArity() == 0;
        return isCloneAndPublic
                && hasNoParameters
                && this.isMethodResultType(node, Object.class)
                && this.isMethodThrowingType(node, CloneNotSupportedException.class);
    }

    private boolean modifiersChanged(ASTMethodDeclaration node) {
        Class<?> type = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class).getType();
        if (type == null) {
            return false;
        }

        String overriddenMethodName = node.getName();

        List<Class<?>> typeArguments = new ArrayList<>();
        for (ASTFormalParameter parameter : node.getFormalParameters()) {
            Class<?> parameterType = parameter.getType();
            if (parameterType != null) {
                typeArguments.add(parameterType);
            }
        }

        // did we have for each parameter the type?
        if (typeArguments.size() != node.getFormalParameters().size()) {
            return false;
        }

        // search method with same name up the hierarchy
        Class<?>[] typeArgumentArray = typeArguments.toArray(new Class<?>[0]);
        Class<?> superType = type.getSuperclass();
        Method declaredMethod = null;
        while (superType != null && declaredMethod == null) {
            try {
                declaredMethod = superType.getDeclaredMethod(overriddenMethodName, typeArgumentArray);
            } catch (NoSuchMethodException | SecurityException e) {
                declaredMethod = null;
            }
            superType = superType.getSuperclass();
        }

        return declaredMethod != null && isElevatingAccessModifier(node, declaredMethod);
    }

    private boolean isElevatingAccessModifier(ASTMethodDeclaration overridingMethod, Method superMethod) {
        String superPackageName = null;
        Package p = superMethod.getDeclaringClass().getPackage();
        if (p != null) {
            superPackageName = p.getName();
        }
        // Note: can't simply compare superMethod.getModifiers() with overridingMethod.getModifiers()
        // since AccessNode#PROTECTED != Modifier#PROTECTED.
        boolean elevatingFromProtected = Modifier.isProtected(superMethod.getModifiers())
                && !overridingMethod.isProtected();
        boolean elevatingFromPackagePrivate = superMethod.getModifiers() == 0 && overridingMethod.getModifiers() != 0;
        boolean elevatingIntoDifferentPackage = !packageName.equals(superPackageName);

        return elevatingFromProtected
                || elevatingFromPackagePrivate
                || elevatingIntoDifferentPackage;
    }

    /**
     * @deprecated this method will be removed. Just use {@link Node#findChildrenOfType(Class)} directly.
     */
    @Deprecated
    public <T> List<T> findFirstDegreeChaildrenOfType(Node n, Class<T> targetType) {
        return n.findChildrenOfType(targetType);
    }
}
