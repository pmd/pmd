package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTLiteral;

public class SuspiciousOctalEscape extends AbstractRule {

    public Object visit(ASTLiteral node, Object data) {
        String image = node.getImage();
        if (image != null && image.startsWith("\"")) // make sure it's a string literal
        {
            // trim quotes
            String s = image.substring(1, image.length() - 1);

            // process escape sequences
            int offset = 0;
            for (int slash = s.indexOf('\\', offset);
                 slash != -1 && slash < s.length() - 1;
                 slash = s.indexOf('\\', offset)) {
                String escapeSequence = s.substring(slash + 1);
                char first = escapeSequence.charAt(0);
                if (isOctal(first)) {
                    if (escapeSequence.length() > 1) {
                        char second = escapeSequence.charAt(1);
                        if (isOctal(second)) {
                            if (escapeSequence.length() > 2) {
                                char third = escapeSequence.charAt(2);
                                if (isOctal(third)) {
                                    // this is either a three digit octal escape or a two-digit
                                    // octal escape followed by an octal digit. the value of
                                    // the first digit in the sequence determines which is the
                                    // case
                                    if (first != '0' && first != '1' && first != '2' && first != '3') {
                                        // VIOLATION: it's a two-digit octal escape followed by
                                        // an octal digit -- legal but very confusing!
                                        addViolation(data, node);
                                    } else {
                                        // if there is a 4th decimal digit, it could never be part of
                                        // the escape sequence, which is confusing
                                        if (escapeSequence.length() > 3) {
                                            char fourth = escapeSequence.charAt(3);
                                            if (isDecimal(fourth)) {
                                                addViolation(data, node);
                                            }
                                        }
                                    }

                                } else if (isDecimal(third)) {
                                    // this is a two-digit octal escape followed by a decimal digit
                                    // legal but very confusing
                                    addViolation(data, node);
                                }
                            }
                        } else if (isDecimal(second)) {
                            // this is a one-digit octal escape followed by a decimal digit
                            // legal but very confusing
                            addViolation(data, node);
                        }
                    }
                }

                offset = slash + 1;
            }
        }

        return super.visit(node, data);
    }

    private boolean isOctal(char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                return true;
            default:
                return false;
        }
    }

    private boolean isDecimal(char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;
        }
    }
}
