/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.rule.bestpractices;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.velocity.ast.ASTBlock;
import net.sourceforge.pmd.lang.velocity.ast.ASTDirective;
import net.sourceforge.pmd.lang.velocity.ast.ASTReference;
import net.sourceforge.pmd.lang.velocity.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.velocity.rule.AbstractVtlRule;

public class UnusedMacroParameterRule extends AbstractVtlRule {

    @Override
    public Object visit(final ASTDirective node, final Object data) {
        if ("macro".equals(node.getDirectiveName())) {
            final Set<String> paramNames = new HashSet<>();
            for (final ASTReference param : node.children(ASTReference.class)) {
                paramNames.add(param.literal());
            }
            final ASTBlock macroBlock = node.firstChild(ASTBlock.class);
            if (macroBlock != null) {
                for (final ASTReference referenceInMacro : macroBlock.descendants(ASTReference.class)) {
                    checkForParameter(paramNames, referenceInMacro.literal());
                }
                for (final ASTStringLiteral literalInMacro : macroBlock.descendants(ASTStringLiteral.class)) {
                    final String text = literalInMacro.literal();
                    checkForParameter(paramNames, text);
                }
            }
            if (!paramNames.isEmpty()) {
                asCtx(data).addViolation(node, paramNames.toString());
            }
        }
        return super.visit(node, data);
    }

    private void checkForParameter(final Set<String> paramNames, final String nameToSearch) {
        final Set<String> paramsContained = new HashSet<>();
        for (final String param : paramNames) {
            if (containsAny(nameToSearch, formatNameVariations(param))) {
                paramsContained.add(param);
            }
        }
        paramNames.removeAll(paramsContained);
    }

    private boolean containsAny(final String text, final String[] formatNameVariations) {
        for (final String formattedName : formatNameVariations) {
            if (text.contains(formattedName)) {
                return true;
            }
        }
        return false;
    }

    private String[] formatNameVariations(final String param) {
        final String actualName = param.substring(1);
        return new String[] { param, "${" + actualName + "}", "${" + actualName + ".", "$!" + actualName,
            "$!{" + actualName + ".", "$!{" + actualName + "}", };
    }
}
