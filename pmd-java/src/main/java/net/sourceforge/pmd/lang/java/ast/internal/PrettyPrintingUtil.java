/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArrayType;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

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

            prettyPrintTypeNode(param.getTypeNode(), sb);
            int extraDimensions = ASTList.sizeOrZero(param.getVarId().getExtraDimensions());
            while (extraDimensions-- > 0) {
                sb.append("[]");
            }
        }

        sb.append(')');

        return sb.toString();
    }

    private static void prettyPrintTypeNode(ASTType t, StringBuilder sb) {
        if (t instanceof ASTPrimitiveType) {
            sb.append(((ASTPrimitiveType) t).getKind().getSimpleName());
        } else if (t instanceof ASTClassOrInterfaceType) {
            sb.append(((ASTClassOrInterfaceType) t).getSimpleName());
        } else if (t instanceof ASTArrayType) {
            prettyPrintTypeNode(((ASTArrayType) t).getElementType(), sb);
            int depth = ((ASTArrayType) t).getArrayDepth();
            for (int i = 0; i < depth; i++) {
                sb.append("[]");
            }
        }
    }

    /**
     * Returns a normalized method name. This just looks at the image of the types of the parameters.
     */
    public static String displaySignature(ASTMethodOrConstructorDeclaration node) {
        ASTFormalParameters params = node.getFormalParameters();
        String name = node instanceof ASTMethodDeclaration ? node.getName() : node.getImage();

        return displaySignature(name, params);
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
}
