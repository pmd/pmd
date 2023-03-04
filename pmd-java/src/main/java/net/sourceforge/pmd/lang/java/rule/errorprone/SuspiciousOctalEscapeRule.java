/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class SuspiciousOctalEscapeRule extends AbstractJavaRulechainRule {

    public SuspiciousOctalEscapeRule() {
        super(ASTStringLiteral.class);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        Chars image = node.getLiteralText();
        // trim quotes
        Chars s = image.subSequence(1, image.length() - 1);

        // process escape sequences
        int offset = 0;
        for (int slash = s.indexOf('\\', offset); slash != -1
                && slash < s.length() - 1; slash = s.indexOf('\\', offset)) {
            Chars escapeSequence = s.subSequence(slash + 1);
            char first = escapeSequence.charAt(0);
            offset = slash + 1; // next offset - after slash

            if (isOctal(first)) {
                if (escapeSequence.length() > 1) {
                    char second = escapeSequence.charAt(1);
                    if (isOctal(second)) {
                        if (escapeSequence.length() > 2) {
                            char third = escapeSequence.charAt(2);
                            if (isOctal(third)) {
                                // this is either a three digit octal escape
                                // or a two-digit
                                // octal escape followed by an octal digit.
                                // the value of
                                // the first digit in the sequence
                                // determines which is the
                                // case
                                if (first != '0' && first != '1' && first != '2' && first != '3') {
                                    // VIOLATION: it's a two-digit octal
                                    // escape followed by
                                    // an octal digit -- legal but very
                                    // confusing!
                                    addViolation(data, node, "\\" + first + second + " + " + third);
                                } else {
                                    // if there is a 4th decimal digit, it
                                    // could never be part of
                                    // the escape sequence, which is
                                    // confusing
                                    if (escapeSequence.length() > 3) {
                                        char fourth = escapeSequence.charAt(3);
                                        if (isDecimal(fourth)) {
                                            addViolation(data, node, "\\" + first + second + third + " + " + fourth);
                                        }
                                    }
                                }

                            } else if (isDecimal(third)) {
                                // this is a two-digit octal escape followed
                                // by a decimal digit
                                // legal but very confusing
                                addViolation(data, node, "\\" + first + second + " + " + third);
                            }
                        }
                    } else if (isDecimal(second)) {
                        // this is a one-digit octal escape followed by a
                        // decimal digit
                        // legal but very confusing
                        addViolation(data, node, "\\" + first + " + " + second);
                    }
                }
            } else if (first == '\\') {
                offset++;
            }
        }

        return data;
    }

    private boolean isOctal(char c) {
        return c >= '0' && c <= '7';
    }

    private boolean isDecimal(char c) {
        return c >= '0' && c <= '9';
    }
}
