/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.AutoFixable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Flags missing @Override annotations.
 *
 * @author Clément Fournier
 * @since 6.2.0
 */
public class MissingOverrideRule extends AbstractJavaRulechainRule implements AutoFixable {

    private final List<ASTMethodDeclaration> violations = new ArrayList<>();

    public MissingOverrideRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isOverridden() && !node.isAnnotationPresent(Override.class)) {
            asCtx(data).addViolation(node, PrettyPrintingUtil.displaySignature(node));
            violations.add(node);
        }
        return data;
    }

    @Override
    public String apply(String rawSource, File file) {
        if (violations.isEmpty()) {
            return rawSource;
        }

        String[] lines = rawSource.split("\\r?\\n");
        List<String> lineList = new ArrayList<>(Arrays.asList(lines));

        // Process violations in reverse line order to maintain correct line numbers
        violations.sort((m1, m2) -> Integer.compare(m2.getBeginLine(), m1.getBeginLine()));

        for (ASTMethodDeclaration method : violations) {
            int lineNum = method.getBeginLine() - 1; // convert to 0-based index
            if (lineNum >= 0 && lineNum < lineList.size()) {
                String line = lineList.get(lineNum);
                String indentation = line.substring(0, line.indexOf(line.trim()));
                String newLine = indentation + "@Override" + System.lineSeparator() + line;
                lineList.set(lineNum, newLine);
            }
        }

        return String.join(System.lineSeparator(), lineList);
    }
}