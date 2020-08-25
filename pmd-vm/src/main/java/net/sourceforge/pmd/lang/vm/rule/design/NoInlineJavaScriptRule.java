/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.design;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.vm.ast.ASTText;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class NoInlineJavaScriptRule extends AbstractVmRule {
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script\\s[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SRC_PATTERN = Pattern.compile("\\ssrc\\s*=", Pattern.CASE_INSENSITIVE);

    @Override
    public Object visit(final ASTText node, final Object data) {
        final Matcher matcher = SCRIPT_PATTERN.matcher(node.literal());
        while (matcher.find()) {
            final String currentMatch = matcher.group();
            if (!SRC_PATTERN.matcher(currentMatch).find()) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}
