/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidHardcodingIdRule extends AbstractApexRule {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]{5}0[a-zA-Z0-9]{9}([a-zA-Z0-5]{3})?$");
    private static final Map<String, Character> CHECKSUM_LOOKUP;

    static {
        final Map<String, Character> lookup = new HashMap<>();
        final char[] chartable = "ABCDEFGHIJKLMNOPQRSTUVWXYZ012345".toCharArray();

        for (int i = 0; i < chartable.length; i++) {
            lookup.put(String.format("%5s", Integer.toBinaryString(i)).replace(' ', '0'), chartable[i]);
        }

        CHECKSUM_LOOKUP = Collections.unmodifiableMap(lookup);
    }

    public AvoidHardcodingIdRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Style");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);

        addRuleChainVisit(ASTLiteralExpression.class);
    }

    @Override
    public Object visit(ASTLiteralExpression node, Object data) {
        if (node.isString()) {
            String literal = node.getImage();
            if (PATTERN.matcher(literal).matches()) {
                // 18-digit ids are just 15 digit ids + checksums, validate it  or it's not an id
                if (literal.length() == 18 && !validateChecksum(literal)) {
                    return data;
                }
                addViolation(data, node);
            }
        }
        return data;
    }

    /*
     * ID validation - sources:
     * https://stackoverflow.com/questions/9742913/validating-a-salesforce-id#answer-29299786
     * https://gist.github.com/jeriley/36b29f7c46527af4532aaf092c90dd56
     */
    private boolean validateChecksum(String literal) {
        final String part1 = literal.substring(0, 5);
        final String part2 = literal.substring(5, 10);
        final String part3 = literal.substring(10, 15);

        final char checksum1 = checksum(part1);
        final char checksum2 = checksum(part2);
        final char checksum3 = checksum(part3);

        return literal.charAt(15) == checksum1 && literal.charAt(16) == checksum2
                && literal.charAt(17) == checksum3;
    }

    private char checksum(String part) {
        final StringBuilder sb = new StringBuilder(5);
        for (int i = 4; i >= 0; i--) {
            sb.append(Character.isUpperCase(part.charAt(i)) ? '1' : '0');
        }
        return CHECKSUM_LOOKUP.get(sb.toString());
    }
}
