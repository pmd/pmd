/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import static net.sourceforge.pmd.util.AssertionUtil.shouldNotReachHere;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTArrayType;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIntersectionType;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnionType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
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
        } else if (t instanceof ASTClassOrInterfaceType) {
            ASTClassOrInterfaceType classT = (ASTClassOrInterfaceType) t;
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
    public static String displaySignature(ASTMethodOrConstructorDeclaration node) {
        return displaySignature(node.getName(), node.getFormalParameters());
    }

    /**
     * Returns the generic kind of declaration this is, eg "enum" or "class".
     */
    public static String getPrintableNodeKind(ASTAnyTypeDeclaration decl) {
        if (decl instanceof ASTClassOrInterfaceDeclaration && decl.isInterface()) {
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
        } else if (node instanceof ASTMethodOrConstructorDeclaration) {
            // constructors are differentiated by their parameters, while we only use method name for methods
            return displaySignature((ASTConstructorDeclaration) node);
        } else if (node instanceof ASTFieldDeclaration) {
            return ((ASTFieldDeclaration) node).getVarIds().firstOrThrow().getName();
        } else if (node instanceof ASTResource) {
            return ((ASTResource) node).getStableName();
        } else if (node instanceof ASTAnyTypeDeclaration) {
            return ((ASTAnyTypeDeclaration) node).getSimpleName();
        } else if (node instanceof ASTVariableDeclaratorId) {
            return ((ASTVariableDeclaratorId) node).getName();
        } else {
            return node.getImage(); // todo get rid of this
        }
    }


    /**
     * Returns the 'kind' of node this is. For instance for a {@link ASTFieldDeclaration},
     * returns "field".
     *
     * @throws UnsupportedOperationException If unimplemented for a node kind
     * @see #getPrintableNodeKind(ASTAnyTypeDeclaration)
     */
    public static String getPrintableNodeKind(JavaNode node) {
        if (node instanceof ASTAnyTypeDeclaration) {
            return getPrintableNodeKind((ASTAnyTypeDeclaration) node);
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


    public static CharSequence prettyPrint(JavaNode node) {
        StringBuilder sb = new StringBuilder();
        node.acceptVisitor(new ExprPrinter(), sb);
        return sb;
    }

    static class ExprPrinter extends JavaVisitorBase<StringBuilder, Void> {

        private static final int MAX_ARG_LENGTH = 20;

        @Override
        public Void visitJavaNode(JavaNode node, StringBuilder data) {
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
        public Void visit(ASTMethodCall node, StringBuilder sb) {
            addQualifier(node, sb);
            sb.append(node.getMethodName());
            if (node.getArguments().isEmpty()) {
                sb.append("()");
            } else {
                final int argStart = sb.length();
                sb.append('(');
                boolean first = true;
                for (ASTExpression arg : node.getArguments()) {
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
            return null;
        }


        private void addQualifier(QualifiableExpression node, StringBuilder data) {
            ASTExpression qualifier = node.getQualifier();
            if (qualifier != null) {
                if (!(qualifier instanceof ASTPrimaryExpression)) {
                    ppInParens(data, qualifier);
                } else {
                    qualifier.acceptVisitor(this, data);
                }
                data.append('.');
            }

        }

        private StringBuilder ppInParens(StringBuilder data, JavaNode qualifier) {
            data.append('(');
            qualifier.acceptVisitor(this, data);
            return data.append(')');
        }

    }


}
