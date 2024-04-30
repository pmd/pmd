/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.Necessity;
import static net.sourceforge.pmd.lang.java.rule.codestyle.UselessParenthesesRule.needsParentheses;
import static net.sourceforge.pmd.util.AssertionUtil.shouldNotReachHere;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTArrayType;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIntersectionType;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameterList;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnionType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.ASTVoidType;
import net.sourceforge.pmd.lang.java.ast.ASTWildcardType;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint.TypePrettyPrinter;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * @author Clément Fournier
 */
public final class PrettyPrintingUtil {

    private PrettyPrintingUtil() {
        // util class
    }

    /**
     * Returns a normalized method name. This just looks at the image of the types of the parameters.
     */
    public static String displaySignature(String methodName, ASTFormalParameters params) {

        StringBuilder sb = new StringBuilder();
        sb.append(methodName);
        sb.append('(');

        boolean first = true;
        for (ASTFormalParameter param : params) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            prettyPrintTypeNode(sb, param.getTypeNode());
            int extraDimensions = ASTList.sizeOrZero(param.getVarId().getExtraDimensions());
            while (extraDimensions-- > 0) {
                sb.append("[]");
            }
        }

        sb.append(')');

        return sb.toString();
    }

    private static void prettyPrintTypeNode(StringBuilder sb, ASTType t) {
        if (t instanceof ASTPrimitiveType) {
            sb.append(((ASTPrimitiveType) t).getKind().getSimpleName());
        } else if (t instanceof ASTClassType) {
            ASTClassType classT = (ASTClassType) t;
            sb.append(classT.getSimpleName());

            ASTTypeArguments targs = classT.getTypeArguments();
            if (targs != null) {
                sb.append("<");
                CollectionUtil.joinOn(sb, targs.toStream(), PrettyPrintingUtil::prettyPrintTypeNode, ", ");
                sb.append(">");
            }
        } else if (t instanceof ASTArrayType) {
            prettyPrintTypeNode(sb, ((ASTArrayType) t).getElementType());
            int depth = ((ASTArrayType) t).getArrayDepth();
            for (int i = 0; i < depth; i++) {
                sb.append("[]");
            }
        } else if (t instanceof ASTVoidType) {
            sb.append("void");
        } else if (t instanceof ASTWildcardType) {
            sb.append("?");
            ASTReferenceType bound = ((ASTWildcardType) t).getTypeBoundNode();
            if (bound != null) {
                sb.append(((ASTWildcardType) t).isLowerBound() ? " super " : " extends ");
                prettyPrintTypeNode(sb, bound);
            }
        } else if (t instanceof ASTUnionType) {
            CollectionUtil.joinOn(sb, ((ASTUnionType) t).getComponents(),
                PrettyPrintingUtil::prettyPrintTypeNode, " | ");
        } else if (t instanceof ASTIntersectionType) {
            CollectionUtil.joinOn(sb, ((ASTIntersectionType) t).getComponents(),
                PrettyPrintingUtil::prettyPrintTypeNode, " & ");
        } else if (t instanceof ASTAmbiguousName) {
            sb.append(((ASTAmbiguousName) t).getName());
        } else {
            throw shouldNotReachHere("Unhandled type? " + t);
        }
    }

    public static String prettyPrintType(ASTType t) {
        StringBuilder sb = new StringBuilder();
        prettyPrintTypeNode(sb, t);
        return sb.toString();
    }

    /**
     * Returns a normalized method name. This just looks at the image of the types of the parameters.
     */
    public static String displaySignature(ASTExecutableDeclaration node) {
        return displaySignature(node.getName(), node.getFormalParameters());
    }

    /**
     * Returns the generic kind of declaration this is, eg "enum" or "class".
     */
    public static String getPrintableNodeKind(ASTTypeDeclaration decl) {
        if (decl instanceof ASTClassDeclaration && decl.isInterface()) {
            return "interface";
        } else if (decl instanceof ASTAnnotationTypeDeclaration) {
            return "annotation";
        } else if (decl instanceof ASTEnumDeclaration) {
            return "enum";
        } else if (decl instanceof ASTRecordDeclaration) {
            return "record";
        }
        return "class";
    }

    /**
     * Returns the "name" of a node. For methods and constructors, this
     * may return a signature with parameters.
     */
    public static String getNodeName(JavaNode node) {
        // constructors are differentiated by their parameters, while we only use method name for methods
        if (node instanceof ASTMethodDeclaration) {
            return ((ASTMethodDeclaration) node).getName();
        } else if (node instanceof ASTExecutableDeclaration) {
            // constructors are differentiated by their parameters, while we only use method name for methods
            return displaySignature((ASTConstructorDeclaration) node);
        } else if (node instanceof ASTFieldDeclaration) {
            return ((ASTFieldDeclaration) node).getVarIds().firstOrThrow().getName();
        } else if (node instanceof ASTResource) {
            ASTLocalVariableDeclaration var = ((ASTResource) node).asLocalVariableDeclaration();
            if (var != null) {
                return var.getVarIds().firstOrThrow().getName();
            } else {
                return PrettyPrintingUtil.prettyPrint(((ASTResource) node).getInitializer()).toString();
            }
        } else if (node instanceof ASTTypeDeclaration) {
            return ((ASTTypeDeclaration) node).getSimpleName();
        } else if (node instanceof ASTVariableId) {
            return ((ASTVariableId) node).getName();
        } else {
            throw new IllegalArgumentException("Node has no defined name: " + node);
        }
    }


    /**
     * Returns the 'kind' of node this is. For instance for a {@link ASTFieldDeclaration},
     * returns "field".
     *
     * @throws UnsupportedOperationException If unimplemented for a node kind
     * @see #getPrintableNodeKind(ASTTypeDeclaration)
     */
    public static String getPrintableNodeKind(JavaNode node) {
        if (node instanceof ASTTypeDeclaration) {
            return getPrintableNodeKind((ASTTypeDeclaration) node);
        } else if (node instanceof ASTMethodDeclaration) {
            return "method";
        } else if (node instanceof ASTConstructorDeclaration) {
            return "constructor";
        } else if (node instanceof ASTFieldDeclaration) {
            return "field";
        } else if (node instanceof ASTResource) {
            return "resource specification";
        }
        throw new UnsupportedOperationException("Node " + node + " is unaccounted for");
    }

    public static String prettyImport(ASTImportDeclaration importDecl) {
        String name = importDecl.getImportedName();
        if (importDecl.isImportOnDemand()) {
            return name + ".*";
        }
        return name;
    }

    /**
     * Pretty print the selected overload.
     */
    public static @NonNull String prettyPrintOverload(ASTMethodCall it) {
        return prettyPrintOverload(it.getOverloadSelectionInfo().getMethodType());
    }

    public static @NonNull String prettyPrintOverload(JMethodSymbol it) {
        return prettyPrintOverload(it.getTypeSystem().sigOf(it));
    }

    public static @NonNull String prettyPrintOverload(JMethodSig it) {
        return TypePrettyPrint.prettyPrint(it, overloadPrinter());
    }

    private static TypePrettyPrinter overloadPrinter() {
        return new TypePrettyPrinter().qualifyNames(false).printMethodResult(false);
    }


    /** Pretty print an expression or any other kind of node. */
    public static CharSequence prettyPrint(JavaNode node) {
        StringBuilder sb = new StringBuilder();
        node.acceptVisitor(new ExprPrinter(), sb);
        return sb;
    }

    static class ExprPrinter extends JavaVisitorBase<StringBuilder, Void> {

        private static final int MAX_ARG_LENGTH = 20;

        @Override
        public Void visitJavaNode(JavaNode node, StringBuilder data) {
            data.append("<<NOT_IMPLEMENTED: ").append(node).append(">>");
            return null; // don't recurse
        }

        @Override
        public Void visit(ASTTypeExpression node, StringBuilder data) {
            node.getTypeNode().acceptVisitor(this, data);
            return null;
        }

        @Override
        public Void visit(ASTCastExpression node, StringBuilder data) {
            ppInParens(data, node.getCastType()).append(' ');
            node.getOperand().acceptVisitor(this, data);
            return null;
        }

        @Override
        public Void visit(ASTClassLiteral node, StringBuilder data) {
            node.getTypeNode().acceptVisitor(this, data);
            data.append(".class");
            return null;
        }

        @Override
        public Void visitLiteral(ASTLiteral node, StringBuilder data) {
            data.append(node.getText());
            return null;
        }

        @Override
        public Void visit(ASTFieldAccess node, StringBuilder data) {
            addQualifier(node, data);
            data.append(node.getName());
            return null;
        }

        @Override
        public Void visit(ASTVariableAccess node, StringBuilder data) {
            data.append(node.getName());
            return null;
        }

        @Override
        public Void visit(ASTThisExpression node, StringBuilder data) {
            if (node.getQualifier() != null) {
                node.getQualifier().acceptVisitor(this, data);
                data.append('.');
            }
            data.append("this");
            return null;
        }

        @Override
        public Void visit(ASTSuperExpression node, StringBuilder data) {
            if (node.getQualifier() != null) {
                node.getQualifier().acceptVisitor(this, data);
                data.append('.');
            }
            data.append("super");
            return null;
        }

        @Override
        public Void visit(ASTArrayAccess node, StringBuilder data) {
            node.getQualifier().acceptVisitor(this, data);
            data.append('[');
            node.getIndexExpression().acceptVisitor(this, data);
            data.append(']');
            return null;
        }

        @Override
        public Void visitType(ASTType node, StringBuilder data) {
            prettyPrintTypeNode(data, node);
            return null;
        }

        @Override
        public Void visit(ASTAmbiguousName node, StringBuilder data) {
            data.append(node.getName());
            return null;
        }

        @Override
        public Void visit(ASTInfixExpression node, StringBuilder sb) {
            printWithParensIfNecessary(node.getLeftOperand(), sb, node);
            sb.append(' ');
            sb.append(node.getOperator());
            sb.append(' ');
            printWithParensIfNecessary(node.getRightOperand(), sb, node);
            return null;
        }


        @Override
        public Void visit(ASTUnaryExpression node, StringBuilder sb) {
            sb.append(node.getOperator());
            printWithParensIfNecessary(node.getOperand(), sb, node);
            return null;
        }

        private void printWithParensIfNecessary(ASTExpression operand, StringBuilder sb, ASTExpression parent) {
            if (operand.isParenthesized() && needsParentheses(operand, parent) != Necessity.NEVER) {
                ppInParens(sb, operand);
            } else {
                operand.acceptVisitor(this, sb);
            }
        }

        @Override
        public Void visit(ASTConditionalExpression node, StringBuilder sb) {
            printWithParensIfNecessary(node.getCondition(), sb, node);
            sb.append(" ? ");
            printWithParensIfNecessary(node.getThenBranch(), sb, node);
            sb.append(" : ");
            printWithParensIfNecessary(node.getElseBranch(), sb, node);
            return null;
        }

        @Override
        public Void visit(ASTLambdaExpression node, StringBuilder sb) {
            node.getParameters().acceptVisitor(this, sb);
            sb.append(" -> ");
            ASTExpression exprBody = node.getExpressionBody();
            if (exprBody != null) {
                exprBody.acceptVisitor(this, sb);
            } else {
                sb.append("{ ... }");
            }
            return null;
        }

        @Override
        public Void visit(ASTLambdaParameterList node, StringBuilder sb) {
            if (node.size() == 1) {
                sb.append(node.get(0).getVarId().getName());
                return null;
            } else if (node.isEmpty()) {
                sb.append("()");
                return null;
            }

            sb.append('(');
            sb.append(node.get(0).getVarId().getName());
            node.toStream().drop(1).forEach(it -> {
                sb.append(", ");
                sb.append(it.getVarId().getName());
            });
            sb.append(')');

            return null;
        }

        @Override
        public Void visit(ASTMethodCall node, StringBuilder sb) {
            addQualifier(node, sb);
            ppTypeArgs(sb, node.getExplicitTypeArguments());
            sb.append(node.getMethodName());
            ppArguments(sb, node.getArguments());
            return null;
        }

        @Override
        public Void visit(ASTConstructorCall node, StringBuilder sb) {
            addQualifier(node, sb);
            sb.append("new ");
            ppTypeArgs(sb, node.getExplicitTypeArguments());
            prettyPrintTypeNode(sb, node.getTypeNode());
            ppArguments(sb, node.getArguments());
            return null;
        }

        private void ppArguments(StringBuilder sb, ASTArgumentList arguments) {
            if (arguments.isEmpty()) {
                sb.append("()");
            } else {
                final int argStart = sb.length();
                sb.append('(');
                boolean first = true;
                for (ASTExpression arg : arguments) {
                    if (sb.length() - argStart >= MAX_ARG_LENGTH) {
                        sb.append("...");
                        break;
                    } else if (!first) {
                        sb.append(", ");
                    }
                    arg.acceptVisitor(this, sb);
                    first = false;
                }
                sb.append(')');
            }
        }

        @Override
        public Void visit(ASTMethodReference node, StringBuilder sb) {
            printWithParensIfNecessary(node.getQualifier(), sb, node);
            sb.append("::");
            ppTypeArgs(sb, node.getExplicitTypeArguments());
            sb.append(node.getMethodName());
            return null;
        }

        private void addQualifier(QualifiableExpression node, StringBuilder data) {
            ASTExpression qualifier = node.getQualifier();
            if (qualifier != null) {
                printWithParensIfNecessary(qualifier, data, node);
                data.append('.');
            }

        }

        private StringBuilder ppInParens(StringBuilder data, JavaNode qualifier) {
            data.append('(');
            qualifier.acceptVisitor(this, data);
            return data.append(')');
        }


        private void ppTypeArgs(StringBuilder data, @Nullable ASTTypeArguments targs) {
            if (targs == null) {
                return;
            }
            data.append('<');
            prettyPrintTypeNode(data, targs.get(0));
            for (int i = 1; i < targs.size(); i++) {
                data.append(", ");
                prettyPrintTypeNode(data, targs.get(i));
            }
            data.append('>');
        }

    }


}
