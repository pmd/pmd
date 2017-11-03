/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.bestpractices;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.vm.ast.ASTBlock;
import net.sourceforge.pmd.lang.vm.ast.ASTDirective;
import net.sourceforge.pmd.lang.vm.ast.ASTReference;
import net.sourceforge.pmd.lang.vm.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class UnusedMacroParameterRule extends AbstractVmRule {

    @Override
    public Object visit(final ASTDirective node, final Object data) {
        if ("macro".equals(node.getDirectiveName())) {
            final Set<String> paramNames = new HashSet<>();
            final List<ASTReference> params = node.findChildrenOfType(ASTReference.class);
            for (final ASTReference param : params) {
                paramNames.add(param.literal());
            }
            final ASTBlock macroBlock = node.getFirstChildOfType(ASTBlock.class);
            if (macroBlock != null) {
                for (final ASTReference referenceInMacro : macroBlock.findDescendantsOfType(ASTReference.class)) {
                    checkForParameter(paramNames, referenceInMacro.literal());
                }
                for (final ASTStringLiteral literalInMacro : macroBlock.findDescendantsOfType(ASTStringLiteral.class)) {
                    final String text = literalInMacro.literal();
                    checkForParameter(paramNames, text);
                }
            }
            if (!paramNames.isEmpty()) {
                addViolation(data, node, paramNames.toString());
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
