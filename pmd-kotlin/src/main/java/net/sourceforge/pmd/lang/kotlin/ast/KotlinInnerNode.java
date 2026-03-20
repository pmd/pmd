/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;

abstract class KotlinInnerNode extends BaseAntlrInnerNode<KotlinNode> implements KotlinNode {

    KotlinInnerNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof KotlinVisitor) {
            // some of the generated antlr nodes have no accept method...
            return ((KotlinVisitor<? super P, ? extends R>) visitor).visitKotlinNode(this, data);
        }
        return visitor.visitNode(this, data);
    }


    @Override // override to make visible in package
    protected PmdAsAntlrInnerNode<KotlinNode> asAntlrNode() {
        return super.asAntlrNode();
    }

    @Override
    public String getXPathNodeName() {
        return KotlinParser.DICO.getXPathNameOfRule(getRuleIndex());
    }

    @Override
    public String toString() {
        // Keep this lightweight and safe for debugging/logging.
        // ANTLR's default ParserRuleContext#toString() often prints raw state numbers,
        // which isn't very usable when debugging.
        String ruleName = getXPathNodeName();

        Token firstToken = getFirstAntlrToken();
        Token lastToken = getLastAntlrToken();

        String text = extractTokenText(firstToken, lastToken);
        String range = extractTokenRange(firstToken, lastToken);

        return ruleName + "[" + range + "]" + (text.isEmpty() ? "" : " '\"" + text + "\"'");
    }

    private static String extractTokenRange(Token firstToken, Token lastToken) {
        String range;
        try {
            if (firstToken != null && lastToken != null) {
                range = firstToken.getLine() + ":" + firstToken.getCharPositionInLine()
                    + "-" + lastToken.getLine() + ":" + lastToken.getCharPositionInLine();
            } else {
                range = "?";
            }
        } catch (Exception ignored) {
            range = "?";
        }
        return range;
    }

    private static String extractTokenText(Token firstToken, Token lastToken) {
        String text = null;
        try {
            if (firstToken != null && lastToken != null
                && firstToken.getInputStream() != null) {

                int start = Math.max(0, firstToken.getStartIndex());
                int stop = Math.max(start - 1, lastToken.getStopIndex());

                // Token indices are inclusive, and may be -1 for synthetic tokens.
                if (stop >= start) {
                    text = firstToken.getInputStream().getText(Interval.of(start, stop));
                }
            }
        } catch (Exception ignored) {
            // ignore
        }
        text = text == null ? "" : text;
        text = escapeNewLines(text);
        text = reduceTextLength(text, 80);
        return text;
    }

    private static String escapeNewLines(String text) {
        return text.replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String reduceTextLength(String text, int maxLen) {
        if (text.length() > maxLen) {
            text = text.substring(0, maxLen) + "…";
        }
        return text;
    }
}
