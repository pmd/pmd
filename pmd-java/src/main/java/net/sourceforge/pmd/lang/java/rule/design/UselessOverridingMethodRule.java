/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * @author Romain Pelisse, bugfix for [ 1522517 ] False +:
 *         UselessOverridingMethod
 */
public class UselessOverridingMethodRule extends AbstractJavaRule {
    private final List<String> exceptions;
    private boolean ignoreAnnotations;
    private static final String CLONE = "clone";
    private static final String OBJECT = "Object";

    // TODO extend AbstractIgnoredAnnotationsRule node
    // TODO ignore if there is javadoc
    private static final PropertyDescriptor<Boolean> IGNORE_ANNOTATIONS_DESCRIPTOR = booleanProperty("ignoreAnnotations").defaultValue(false).desc("Ignore annotations").build();


    public UselessOverridingMethodRule() {
        definePropertyDescriptor(IGNORE_ANNOTATIONS_DESCRIPTOR);

        exceptions = new ArrayList<>(1);
        exceptions.add("CloneNotSupportedException");
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        init();
        return super.visit(node, data);
    }

    private void init() {
        ignoreAnnotations = getProperty(IGNORE_ANNOTATIONS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration clz, Object data) {
        if (clz.isInterface()) {
            return data;
        }
        return super.visit(clz, data);
    }

    // TODO: this method should be externalize into an utility class, shouldn't it ?
    private boolean isMethodType(ASTMethodDeclaration node, String methodType) {
        boolean result = false;
        ASTResultType type = node.getResultType();
        if (type != null) {
            result = type.hasDescendantMatchingXPath(
                    "./Type/ReferenceType/ClassOrInterfaceType[@Image = '" + methodType + "']");
        }
        return result;
    }

    // TODO: this method should be externalize into an utility class, shouldn't it ?
    private boolean isMethodThrowingType(ASTMethodDeclaration node, List<String> exceptedExceptions) {
        boolean result = false;
        ASTNameList thrownsExceptions = node.getFirstChildOfType(ASTNameList.class);
        if (thrownsExceptions != null) {
            List<ASTName> names = thrownsExceptions.findChildrenOfType(ASTName.class);
            for (ASTName name : names) {
                for (String exceptedException : exceptedExceptions) {
                    if (exceptedException.equals(name.getImage())) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private boolean hasArguments(ASTMethodDeclaration node) {
        return node.hasDescendantMatchingXPath("./MethodDeclarator/FormalParameters/*");
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
        if (CLONE.equals(node.getName()) && node.isPublic() && !this.hasArguments(node)
                && this.isMethodType(node, OBJECT) && this.isMethodThrowingType(node, exceptions)) {
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
            List<ASTPrimaryExpression> primaryExpressions = findFirstDegreeChildrenOfType(statementGrandChild,
                    ASTPrimaryExpression.class);
            if (primaryExpressions.size() != 1) {
                return super.visit(node, data);
            }
            primaryExpression = primaryExpressions.get(0);
        }

        ASTPrimaryPrefix primaryPrefix = findFirstDegreeChildrenOfType(primaryExpression, ASTPrimaryPrefix.class)
                .get(0);
        if (!primaryPrefix.usesSuperModifier()) {
            return super.visit(node, data);
        }

        List<ASTPrimarySuffix> primarySuffixList = findFirstDegreeChildrenOfType(primaryExpression,
                ASTPrimarySuffix.class);
        if (primarySuffixList.size() != 2) {
            // extra method call on result of super method
            return super.visit(node, data);
        }

        ASTMethodDeclarator methodDeclarator = findFirstDegreeChildrenOfType(node, ASTMethodDeclarator.class).get(0);
        ASTPrimarySuffix primarySuffix = primarySuffixList.get(0);
        if (!primarySuffix.hasImageEqualTo(methodDeclarator.getImage())) {
            return super.visit(node, data);
        }
        // Process arguments
        primarySuffix = primarySuffixList.get(1);
        ASTArguments arguments = (ASTArguments) primarySuffix.getChild(0);
        ASTFormalParameters formalParameters = (ASTFormalParameters) methodDeclarator.getChild(0);
        if (formalParameters.getNumChildren() != arguments.getNumChildren()) {
            return super.visit(node, data);
        }

        if (!ignoreAnnotations) {
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

        if (arguments.getNumChildren() == 0) {
            addViolation(data, node, getMessage());
        } else {
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

                if (formalParameters.getNumChildren() < i + 1) {
                    return super.visit(node, data); // different number of args
                }

                ASTName argumentName = (ASTName) argumentPrimaryPrefixChild;
                ASTFormalParameter formalParameter = (ASTFormalParameter) formalParameters.getChild(i);
                ASTVariableDeclaratorId variableId = findFirstDegreeChildrenOfType(formalParameter,
                        ASTVariableDeclaratorId.class).get(0);
                if (!argumentName.hasImageEqualTo(variableId.getImage())) {
                    // The arguments are not simply passed through
                    return super.visit(node, data);
                }

            }
            // All arguments are passed through directly
            addViolation(data, node, getMessage());
        }
        return super.visit(node, data);
    }

    public <T> List<T> findFirstDegreeChildrenOfType(Node n, Class<T> targetType) {
        List<T> l = new ArrayList<>();
        lclFindChildrenOfType(n, targetType, l);
        return l;
    }

    private <T> void lclFindChildrenOfType(Node node, Class<T> targetType, List<T> results) {
        if (node.getClass().equals(targetType)) {
            results.add((T) node);
        }

        if (node instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) node).isNested()) {
            return;
        }

        if (node instanceof ASTClassOrInterfaceBodyDeclaration
                && ((ASTClassOrInterfaceBodyDeclaration) node).isAnonymousInnerClass()) {
            return;
        }

        for (int i = 0; i < node.getNumChildren(); i++) {
            Node child = node.getChild(i);
            if (child.getClass().equals(targetType)) {
                results.add((T) child);
            }
        }
    }
}
