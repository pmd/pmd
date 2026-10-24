/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class MethodWithSameNameAsEnclosingClassRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }


    @Override
    public Object visit(ASTUserClass node, Object data) {
        String className = node.getSimpleName();

        for (ASTMethod m : node.descendants(ASTMethod.class)) {
            String methodName = m.getImage();

            if (!m.isConstructor() && methodName.equalsIgnoreCase(className)) {
                asCtx(data).addViolationWithPosition(m, m.getAstInfo(), getReportLocation(m), getMessage());
            }
        }

        return data;
    }

    /**
     * Only the signature of the method is reported, not its body.
     */
    private static FileLocation getReportLocation(ASTMethod node) {
        TextDocument document = node.getAstInfo().getTextDocument();
        ASTBlockStatement body = node.firstChild(ASTBlockStatement.class);
        if (body == null) {
            return node.getReportLocation();
        }
        int start = node.getTextRegion().getStartOffset();
        int end = body.getTextRegion().getStartOffset();
        while (end > start && Character.isWhitespace(document.getText().charAt(end - 1))) {
            end--;
        }
        return document.toLocation(TextRegion.fromBothOffsets(start, end));
    }
}
