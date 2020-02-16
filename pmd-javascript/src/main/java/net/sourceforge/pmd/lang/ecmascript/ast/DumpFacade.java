/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.util.StringUtil;

/**
 *
 * @deprecated This class will be removed with PMD 7. The rule designer is a better way to inspect nodes.
 */
@Deprecated
public class DumpFacade {

    private PrintWriter writer;
    private boolean recurse;

    public void initializeWith(Writer writer, String prefix, boolean recurse, EcmascriptNode<?> node) {
        this.writer = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
        this.recurse = recurse;
        this.dump(node, prefix);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Problem flushing PrintWriter.", e);
        }
    }

    public Object visit(EcmascriptNode<?> node, Object data) {
        dump(node, (String) data);
        if (recurse) {
            for (int i = 0; i < node.getNumChildren(); i++) {
                visit((EcmascriptNode<?>) node.getChild(i), data + " ");
            }
            return data;
        } else {
            return data;
        }
    }

    private void dump(EcmascriptNode<?> node, String prefix) {
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
        image = StringUtil.escapeWhitespace(image);

        // Extras
        List<String> extras = new ArrayList<>();

        // Standard DestructuringNode extras
        if (node instanceof DestructuringNode) {
            if (((DestructuringNode) node).isDestructuring()) {
                extras.add("destructuring");
            }
        }

        // Other extras
        if (node instanceof ASTArrayComprehension) {
            if (((ASTArrayComprehension) node).hasFilter()) {
                extras.add("has filter");
            }
        } else if (node instanceof ASTBreakStatement) {
            if (((ASTBreakStatement) node).hasLabel()) {
                extras.add("has label");
            }
        } else if (node instanceof ASTCatchClause) {
            if (((ASTCatchClause) node).isIf()) {
                extras.add("if");
            }
        } else if (node instanceof ASTContinueStatement) {
            if (((ASTContinueStatement) node).hasLabel()) {
                extras.add("has label");
            }
        } else if (node instanceof ASTExpressionStatement) {
            if (((ASTExpressionStatement) node).hasResult()) {
                extras.add("has result");
            }
        } else if (node instanceof ASTForInLoop) {
            if (((ASTForInLoop) node).isForEach()) {
                extras.add("for each");
            }
        } else if (node instanceof ASTFunctionCall) {
            if (((ASTFunctionCall) node).hasArguments()) {
                extras.add("has arguments");
            }
        } else if (node instanceof ASTFunctionNode) {
            if (((ASTFunctionNode) node).isClosure()) {
                extras.add("closure");
            }
            if (((ASTFunctionNode) node).isGetter()) {
                extras.add("getter");
            }
            if (((ASTFunctionNode) node).isSetter()) {
                extras.add("setter");
            }
        } else if (node instanceof ASTIfStatement) {
            if (((ASTIfStatement) node).hasElse()) {
                extras.add("has else");
            }
        } else if (node instanceof ASTKeywordLiteral) {
            if (((ASTKeywordLiteral) node).isBoolean()) {
                extras.add("boolean");
            }
        } else if (node instanceof ASTLetNode) {
            if (((ASTLetNode) node).hasBody()) {
                extras.add("has body");
            }
        } else if (node instanceof ASTName) {
            if (((ASTName) node).isLocalName()) {
                extras.add("local");
            }
            if (((ASTName) node).isGlobalName()) {
                extras.add("global");
            }
        } else if (node instanceof ASTNewExpression) {
            if (((ASTNewExpression) node).hasArguments()) {
                extras.add("has arguments");
            }
            if (((ASTNewExpression) node).hasInitializer()) {
                extras.add("has initializer");
            }
        } else if (node instanceof ASTNumberLiteral) {
            extras.add("Number=" + ((ASTNumberLiteral) node).getNumber());
            extras.add("NormalizedImage=" + ((ASTNumberLiteral) node).getNormalizedImage());
        } else if (node instanceof ASTObjectProperty) {
            if (((ASTObjectProperty) node).isGetter()) {
                extras.add("getter");
            }
            if (((ASTObjectProperty) node).isSetter()) {
                extras.add("setter");
            }
        } else if (node instanceof ASTRegExpLiteral) {
            extras.add("Flags=" + ((ASTRegExpLiteral) node).getFlags());
        } else if (node instanceof ASTReturnStatement) {
            if (((ASTReturnStatement) node).hasResult()) {
                extras.add("has result");
            }
        } else if (node instanceof ASTStringLiteral) {
            if (((ASTStringLiteral) node).isSingleQuoted()) {
                extras.add("single quoted");
            }
            if (((ASTStringLiteral) node).isDoubleQuoted()) {
                extras.add("double quoted");
            }
        } else if (node instanceof ASTSwitchCase) {
            if (((ASTSwitchCase) node).isDefault()) {
                extras.add("default");
            }
        } else if (node instanceof ASTTryStatement) {
            if (((ASTTryStatement) node).hasCatch()) {
                extras.add("catch");
            }
            if (((ASTTryStatement) node).hasFinally()) {
                extras.add("finally");
            }
        } else if (node instanceof ASTUnaryExpression) {
            if (((ASTUnaryExpression) node).isPrefix()) {
                extras.add("prefix");
            }
            if (((ASTUnaryExpression) node).isPostfix()) {
                extras.add("postfix");
            }
        }

        // Standard EcmascriptNode extras
        if (node.hasSideEffects()) {
            extras.add("has side effects");
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

}
