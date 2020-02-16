/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @deprecated This class will be removed with PMD 7. The rule designer is a better way to inspect nodes.
 */
@Deprecated
public class DumpFacade extends JavaParserVisitorAdapter {

    private PrintWriter writer;
    private boolean recurse;

    public void initializeWith(Writer writer, String prefix, boolean recurse, JavaNode node) {
        this.writer = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
        this.recurse = recurse;
        this.visit(node, prefix);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Problem flushing PrintWriter.", e);
        }
    }

    @Override
    public Object visit(JavaNode node, Object data) {
        dump(node, (String) data);
        if (recurse) {
            return super.visit(node, data + " ");
        } else {
            return data;
        }
    }

    private void dump(JavaNode node, String prefix) {
        //
        // Dump format is generally composed of the following items...
        //

        // 1) Dump prefix
        writer.print(prefix);

        // 2) JJT Name of the Node
        writer.print(node.getXPathNodeName());

        //
        // If there are any additional details, then:
        // 1) A colon
        // 2) The Node.getImage() if it is non-empty
        // 3) Extras in parentheses
        //

        // Standard image handling
        String image = node.getImage();

        // Special image handling (e.g. Nodes with normally null images)
        if (node instanceof ASTBooleanLiteral) {
            image = String.valueOf(((ASTBooleanLiteral) node).isTrue());
        } else if (node instanceof ASTPrimaryPrefix) {
            ASTPrimaryPrefix primaryPrefix = (ASTPrimaryPrefix) node;
            String result = null;
            if (primaryPrefix.usesSuperModifier()) {
                result = "super";
            } else if (primaryPrefix.usesThisModifier()) {
                result = "this";
            }
            if (image != null) {
                result += "." + image;
            }
            image = result;
        } else if (node instanceof ASTPrimarySuffix) {
            ASTPrimarySuffix primarySuffix = (ASTPrimarySuffix) node;
            if (primarySuffix.isArrayDereference()) {
                if (image == null) {
                    image = "[";
                } else {
                    image = "[" + image;
                }
            }
        }

        // Extras
        List<String> extras = new ArrayList<>();

        collectModifiers(node, extras);

        // Standard Dimensionable extras
        if (node instanceof Dimensionable) {
            Dimensionable dimensionable = (Dimensionable) node;
            if (dimensionable.isArray()) {
                StringBuilder extra = new StringBuilder("array");
                for (int i = 0; i < dimensionable.getArrayDepth(); i++) {
                    extra.append('[');
                }
                extras.add(extra.toString());
            }
        }

        // Other extras
        if (node instanceof ASTArguments) {
            extras.add(String.valueOf(((ASTArguments) node).size()));
        } else if (node instanceof ASTAssignmentOperator) {
            extras.add(((ASTAssignmentOperator) node).isCompound() ? "compound" : "simple");
        } else if (node instanceof ASTClassOrInterfaceBodyDeclaration) {
            if (((ASTClassOrInterfaceBodyDeclaration) node).isAnonymousInnerClass()) {
                extras.add("anonymous inner class");
            }
            if (((ASTClassOrInterfaceBodyDeclaration) node).isEnumChild()) {
                extras.add("enum child");
            }
        } else if (node instanceof ASTBlock) {
            if (((ASTBlock) node).containsComment()) {
                extras.add("contains comment");
            }
        } else if (node instanceof ASTClassOrInterfaceDeclaration) {
            extras.add(((ASTClassOrInterfaceDeclaration) node).isInterface() ? "interface" : "class");
            if (((ASTClassOrInterfaceDeclaration) node).isNested()) {
                extras.add("nested");
            }
        } else if (node instanceof ASTConditionalExpression) {
            extras.add("ternary");
        } else if (node instanceof ASTConstructorDeclaration) {
            extras.add(String.valueOf(((ASTConstructorDeclaration) node).getArity()));
            if (((ASTConstructorDeclaration) node).containsComment()) {
                extras.add("contains comment");
            }
        } else if (node instanceof ASTExplicitConstructorInvocation) {
            extras.add(String.valueOf(((ASTExplicitConstructorInvocation) node).getArgumentCount()));
            if (((ASTExplicitConstructorInvocation) node).isThis()) {
                extras.add("this");
            }
            if (((ASTExplicitConstructorInvocation) node).isSuper()) {
                extras.add("super");
            }
        } else if (node instanceof ASTFormalParameter) {
            if (((ASTFormalParameter) node).isVarargs()) {
                extras.add("varargs");
            }
        } else if (node instanceof ASTFormalParameters) {
            extras.add(String.valueOf(((ASTFormalParameters) node).size()));
        } else if (node instanceof ASTIfStatement) {
            if (((ASTIfStatement) node).hasElse()) {
                extras.add("has else");
            }
        } else if (node instanceof ASTImportDeclaration) {
            if (((ASTImportDeclaration) node).isImportOnDemand()) {
                extras.add("on demand");
            }
            if (((ASTImportDeclaration) node).isStatic()) {
                extras.add("static");
            }
        } else if (node instanceof ASTInitializer) {
            extras.add(((ASTInitializer) node).isStatic() ? "static" : "nonstatic");
        } else if (node instanceof ASTLiteral) {
            ASTLiteral literal = (ASTLiteral) node;
            if (literal.isCharLiteral()) {
                extras.add("char style");
            }
            if (literal.isIntLiteral()) {
                extras.add("int style");
            }
            if (literal.isFloatLiteral()) {
                extras.add("float style");
            }
            if (literal.isStringLiteral()) {
                extras.add("String style");
            }
            if (literal.isDoubleLiteral()) {
                extras.add("double style");
            }
            if (literal.isLongLiteral()) {
                extras.add("long style");
            }
        } else if (node instanceof ASTResultType) {
            if (((ASTResultType) node).isVoid()) {
                extras.add("void");
            }
            if (((ASTResultType) node).returnsArray()) {
                extras.add("returns array");
            }
        } else if (node instanceof ASTSwitchLabel) {
            if (((ASTSwitchLabel) node).isDefault()) {
                extras.add("default");
            }
        } else if (node instanceof ASTTryStatement) {
            if (((ASTTryStatement) node).hasFinally()) {
                extras.add("has finally");
            }
        } else if (node instanceof ASTModuleDirective) {
            ASTModuleDirective directive = (ASTModuleDirective) node;
            extras.add(directive.getType());
            if (directive.getRequiresModifier() != null) {
                extras.add(directive.getRequiresModifier());
            }
        }

        // Output image and extras
        if (image != null || !extras.isEmpty()) {
            writer.print(':');
            if (image != null) {
                writer.print(image);
            }
            for (String extra : extras) {
                writer.print('(');
                writer.print(extra);
                writer.print(')');
            }
        }

        writer.println();
    }

    private void collectModifiers(JavaNode node, List<String> extras) {
        // Standard AccessNode extras
        if (node instanceof AccessNode) {
            AccessNode accessNode = (AccessNode) node;
            if (accessNode.isPackagePrivate()) {
                extras.add("package private");
            }
            if (accessNode.isPrivate()) {
                extras.add("private");
            }
            if (accessNode.isPublic()) {
                extras.add("public");
            }
            if (accessNode.isProtected()) {
                extras.add("protected");
            }
            if (accessNode.isAbstract()) {
                extras.add("abstract");
            }
            if (accessNode.isStatic()) {
                extras.add("static");
            }
            if (accessNode.isFinal()) {
                extras.add("final");
            }
            if (accessNode.isSynchronized()) {
                extras.add("synchronized");
            }
            if (accessNode.isNative()) {
                extras.add("native");
            }
            if (accessNode.isStrictfp()) {
                extras.add("strict");
            }
            if (accessNode.isTransient()) {
                extras.add("transient");
            }
            if (accessNode.isDefault()) {
                extras.add("default");
            }
        }
    }
}
