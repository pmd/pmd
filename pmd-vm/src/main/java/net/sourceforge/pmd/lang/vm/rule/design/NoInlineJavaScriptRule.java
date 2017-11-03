/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.design;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.vm.ast.ASTText;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class NoInlineJavaScriptRule extends AbstractVmRule {
    @Override
    public Object visit(final ASTText node, final Object data) {
        final Pattern scriptPattern = Pattern.compile("<script\\s[^>]*>", Pattern.CASE_INSENSITIVE);
        final Pattern srcPattern = Pattern.compile("\\ssrc\\s*=", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = scriptPattern.matcher(node.literal());
        while (matcher.find()) {
            final String currentMatch = matcher.group();
            if (!srcPattern.matcher(currentMatch).find()) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}
